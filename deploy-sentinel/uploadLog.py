import base64
import json
import socket
import time
import requests

__author__ = 'aiguoxin'

"""
    监控proxy代理
"""

def upload_proxy_log():
    ip = socket.gethostbyname(socket.gethostname())
    current_time = int(round(time.time()))
    f = file("/data/watchmen/proxy_status.json")
    proxy_json = json.dumps(json.load(f, "utf-8"), encoding="UTF-8", ensure_ascii=False)
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
