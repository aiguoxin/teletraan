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


def search_page(request):
    # todo get host
    today = datetime.date.today()
    after = today.strftime('%Y-%m-%d 00:00:00')
    before = today.strftime('%Y-%m-%d 23:59:59')
    after = request.POST.get('after', after)
    before = request.POST.get('before', before)
    ip = request.POST.get('ip', '10.1.193.60')
    name = request.POST.get('name', None)

    # search
    xData = []
    yData = []
    proxyDict = {}
    params = [('ip', ip), ('after', date_to_timestamp(after)), ('before', date_to_timestamp(before)), ('name', name)]

    # get proxy
    if ip != '' and name is not None:
        get_proxy_log = graph_helper.get_proxy_log(request, params)
        if get_proxy_log is not None:
            # parse json to map
            # logger.info(get_proxy_log)
            dataDict = json.dumps(get_proxy_log)
            jsonVal = json.loads(dataDict)
            for proxyLog in jsonVal:
                xData.append(timestamp_to_date(proxyLog['createTime']))
                yData.append(proxyLog['successRate'])
    return render(request, 'graph/proxy.html',
                  {"after": after, "before": before, "ip": ip,
                   "name": name, "xData": json.dumps(xData), "yData": json.dumps(yData),
                   "csrf_token": get_token(request)})


def ip_page(request):
    # todo get host
    today = datetime.date.today()
    after = today.strftime('%Y-%m-%d 00:00:00')
    before = today.strftime('%Y-%m-%d 23:59:59')
    after = request.POST.get('after', after)
    before = request.POST.get('before', before)
    ip = request.POST.get('ip', '10.1.193.60')
    name = request.POST.get('name', None)

    # search
    count = 0
    xData = []
    proxyDict = {}
    params = [('ip', ip), ('after', date_to_timestamp(after)), ('before', date_to_timestamp(before)), ('name', name)]
    # get all proxy
    if ip != '':
        get_ip_log = graph_helper.get_ip_log(request, params)
        if get_ip_log is not None:
            # parse json to map
            for key, val in get_ip_log.items():
                proxyData = []
                count += 1
                for i, el in enumerate(val):
                    proxyData.append(el['successRate'])
                    # add once ok
                    if count == 1:
                        xData.append(timestamp_to_date(el['createTime']))
                proxyDict[key] = proxyData
            proxyDict = json.dumps(proxyDict, encoding="UTF-8", ensure_ascii=False)
            logger.info(proxyDict)

    return render(request, 'graph/proxy_ip.html',
                  {"after": after, "before": before, "ip": ip,
                   "name": name, "xData": json.dumps(xData), "proxyDict": proxyDict,
                   "csrf_token": get_token(request)})


def get_proxy(request):
    # return data for ajax calls
    proxy_list = graph_helper.get_proxy_by_id(request)
    return HttpResponse(json.dumps({'proxy': proxy_list}), content_type="application/json")
