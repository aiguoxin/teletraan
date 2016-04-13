# coding:utf-8

import base64
import time
import requests

__author__ = 'aiguoxin'

"""
    监控接口成功率，每次读取最后一行，昨天的数据
"""

def upload_proxy_log():
    line = open('/data/emailZabbixForm/graph/trend.dat').readlines()[-1]
    current_time = int(round(time.time()))
    # logStr = "11/04/2016 99.54 99.36 99.1 99.86 99.75 99.89 99.55 99.88 98.99 99.64 98.78 98.82 97.9 99.64 99.02 99.74 98.6 97.22 97.31 98.8"
    proxy_json = base64.b64encode(line)
    interfaceInfoVo = {}
    upload_url = "http://10.10.11.172:8089/v1/interface"
    headers = {'Content-type': 'application/json', 'Authorization': 'kIIoz6LMR_u_kc6sRx2pDg'}
    interfaceInfoVo['createTime'] = current_time
    interfaceInfoVo['proxyJson'] = proxy_json
    result = requests.post(upload_url, json=interfaceInfoVo, headers=headers)
    print(result)


if __name__ == "__main__":
    upload_proxy_log()
