# coding:utf-8
# Copyright 2016 Pinterest, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# -*- coding: utf-8 -*-
"""Collection of all deploy related views
"""
import json
import logging
import datetime
from django.middleware.csrf import get_token

from django.shortcuts import render
from django.http import HttpResponse
import time
from deploy_board.webapp import common
from deploy_board.webapp.helpers import graph_helper
logger = logging.getLogger(__name__)


def date_to_timestamp(date):
    result = time.strptime(date, '%Y-%m-%d %H:%M:%S')
    return int(time.mktime(result))


def timestamp_to_date(timestamp):
    if timestamp > 10000000000:
        timestamp /= 1000
    x = time.localtime(timestamp)
    return time.strftime('%Y-%m-%d %H:%M:%S', x)


def index_page(request):
    today = datetime.date.today()
    after = today.strftime('%Y-%m-%d 00:00:00')
    before = today.strftime('%Y-%m-%d 23:59:59')
    after = request.POST.get('after', after)
    before = request.POST.get('before', before)
    ip = request.POST.get('ip', '10.1.193.60')
    name = request.POST.get('name', None)

    # 获取折线图数据
    xData = set()
    proxyDict = {}
    params = [('ip', ip), ('name', name), ('after', date_to_timestamp(after)), ('before', date_to_timestamp(before))]
    if name is not None:
        get_interface_log = graph_helper.get_line_graph_data(request, params)
        if get_interface_log is not None:
            for key, val in get_interface_log.items():
                proxyData = []
                for i, el in enumerate(val):
                    proxyData.append(el['codeCount'])
                    xData.add(timestamp_to_date(el['createTime']))
                proxyDict[key] = proxyData
    xData = sorted(list(xData))
    proxyDict = json.dumps(proxyDict, encoding="UTF-8", ensure_ascii=False)

    # 获取饼图数据
    pieDict = {}
    params = [('ip', ip), ('name', name), ('after', date_to_timestamp(after)), ('before', date_to_timestamp(before))]
    if name is not None:
        get_pie_data = graph_helper.get_pie_graph_data(request, params)
        pieDict = json.dumps(get_pie_data, encoding="UTF-8", ensure_ascii=False)
    return render(request, 'code/proxy.html',
              {"after": after, "before": before, "name": name, "ip": ip, "xData": json.dumps(xData),
               "pieDict": pieDict,
               "proxyDict": proxyDict,
               "csrf_token": get_token(request)})


def get_interface(request):
    # return data for ajax calls
    proxy_list = graph_helper.get_ip_interface_list(request)
    return HttpResponse(json.dumps({'proxy': proxy_list}), content_type="application/json")
