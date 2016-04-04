# coding:utf-8
# Copyright 2016 Pinterest, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import os
import subprocess
import traceback
import time
import string
import random
import requests
import json
import base64
import socket


"""
    用于上传war包到server服务器
"""


def main():
    build_path = "http://dlsw.baidu.com/sw-search-sp/soft/2a/25677/QQ_V4.1.1.1456905733.dmg"
    # build_path = "file:///Volumes/work/qiyi/git/views/target/views.war"

    host_info_path = "file://%s/%s" % (os.path.dirname(os.path.realpath(__file__)),
                                       "host_info")
    # build_dest_dir = '/tmp/quickstart-build.tar.gz'
    build_dest_dir = '/tmp/views.war'

    host_info_dest_dir = '/data/deployd/host_info'
    build_download_cmd = ['curl', '-ksS', build_path, '-o', build_dest_dir]
    host_info_download_cmd = ['curl', '-ksS', host_info_path, '-o', host_info_dest_dir]

    try:
        # Publish build
        process = subprocess.Popen(build_download_cmd, stdout=subprocess.PIPE,
                                   stderr=subprocess.PIPE)
        output, error = process.communicate()
        if error:
            print "Error: failed to publish build to /tmp directory.", error
            return

        # Make deployd directory if it doesn't yet exist
        if not os.path.exists("/tmp/deployd"):
            os.makedirs("/tmp/deployd")

        # Copy over host_info  file
        process = subprocess.Popen(host_info_download_cmd, stdout=subprocess.PIPE,
                                   stderr=subprocess.PIPE)
        output, error = process.communicate()
        if error:
            print "Error: failed to publish host_info to /tmp directory.", error
            return
        publish_local_build("file://%s" % build_dest_dir)
    except Exception as e:
        print traceback.format_exc()
        return None, e.message, 1


def main_upload():
    # target = "file:///Volumes/work/qiyi/git/views/target/views.war"
    target = "http://dlsw.baidu.com/sw-search-sp/soft/2a/25677/QQ_V4.1.1.1456905733.dmg"
    host_info_path = "file://%s/%s" % (os.path.dirname(os.path.realpath(__file__)),
                                       "host_info")
    host_info_dest_dir = '/data/deployd/host_info'
    host_info_download_cmd = ['curl', '-ksS', host_info_path, '-o', host_info_dest_dir]

    try:
        # Make deployd directory if it doesn't yet exist
        if not os.path.exists("/data/deployd"):
            os.makedirs("/data/deployd")

        # Copy over host_info  file
        process = subprocess.Popen(host_info_download_cmd, stdout=subprocess.PIPE,
                                   stderr=subprocess.PIPE)
        output, error = process.communicate()
        if error:
            print "Error: failed to publish host_info to /tmp directory.", error
            return
        publish_local_build(target)
        # TODO 直接部署，先不用界面，直接加入jenkins集成
    except Exception as e:
        print traceback.format_exc()
        return None, e.message, 1


def gen_random_num(size=8, chars=string.digits):
    return ''.join(random.choice(chars) for _ in range(size))


def publish_local_build(build_path, build_name='views', branch='master', commit=gen_random_num(32)):
    build = {}
    publish_build_url = "http://localhost:8089/v1/builds"
    headers = {'Content-type': 'application/json', 'Authorization': 'kIIoz6LMR_u_kc6sRx2pDg'}
    build['name'] = build_name
    build['repo'] = build_name
    build['branch'] = branch
    build['commit'] = commit
    build['id'] = "P67"  # 获取jenkins输入TAG
    build['commitDate'] = int(round(time.time() * 1000))  # 时间戳
    build['artifactUrl'] = build_path
    build['publishInfo'] = "http://jenkins_url/view/views/job/views-prod_deploy_all/100/console"
    r = requests.post(publish_build_url, json=build, headers=headers)
    if 200 <= r.status_code < 300:
        print "Successfully published local " + build_name + " build and host_info " \
                                                             "configuration file to local /tmp directory!"
    else:
        print "Error publishing local " + build_name + " build. Status code = %s, response = %s" % (str(r.status_code),
                                                                                                    str(r.text))
    return build


def byteify(input):
    if isinstance(input, dict):
        return {byteify(key): byteify(value) for key, value in input.iteritems()}
    elif isinstance(input, list):
        return [byteify(element) for element in input]
    elif isinstance(input, unicode):
        return input.encode('utf-8')
    else:
        return input


def upload_proxy_log():
    ip = socket.gethostbyname(socket.gethostname())
    current_time = int(round(time.time() * 1000))
    f = file("/data/watchmen/proxy_status.json")
    proxy_json = byteify(json.load(f, "utf-8"))
    print(proxy_json)
    proxy_json = (str(proxy_json)).encode("utf-8")
    proxy_json = base64.b64encode(proxy_json)
    print(proxy_json)
    proxyLogVo = {}
    upload_url = "http://localhost:8089/v1/proxylog"
    headers = {'Content-type': 'application/json', 'Authorization': 'kIIoz6LMR_u_kc6sRx2pDg'}
    proxyLogVo['createTime'] = current_time
    proxyLogVo['ip'] = str(ip)
    proxyLogVo['proxyJson'] = proxy_json
    result = requests.post(upload_url, json=proxyLogVo, headers=headers)
    f.close
    print(result)


if __name__ == "__main__":
    upload_proxy_log()
    # main_upload()