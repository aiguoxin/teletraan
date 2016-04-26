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

def main_upload():
    # target = "file:///Volumes/work/qiyi/git/views/target/views.war"
    target = "http://10.121.75.13:8080/job/views_plt-dev_deploy_current/ws/target/views_plt.war"
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
    build['id'] = "P71"  # 获取jenkins输入TAG
    build['commitDate'] = int(round(time.time() * 1000))  # 时间戳
    build['artifactUrl'] = build_path
    build['publishInfo'] = "http://10.121.75.13:8080/job/views_plt-dev_deploy_current/55/console"
    r = requests.post(publish_build_url, json=build, headers=headers)
    print r
    if 200 <= r.status_code < 300:
        print "Successfully published local " + build_name + " build and host_info " \
                                                             "configuration file to local /tmp directory!"
    else:
        print "Error publishing local " + build_name + " build. Status code = %s, response = %s" % (str(r.status_code),
                                                                                                    str(r.text))
    return build


if __name__ == "__main__":
    main_upload()