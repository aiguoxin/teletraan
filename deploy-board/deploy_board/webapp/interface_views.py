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


def search_page(request):
    today = datetime.date.today()
    days = datetime.timedelta(days=7)
    days_before = today - days
    after = days_before.strftime('%Y-%m-%d')
    before = today.strftime('%Y-%m-%d')
    after = request.POST.get('after', after)
    before = request.POST.get('before', before)
    name = request.POST.get('name', None)
    flag = request.POST.get('flag', 0)

    # search
    xData = []
    yData = []
    params = [('flag', flag), ('after', after), ('before', before), ('name', name)]

    # get proxy
    if name is not None:
        get_interface_log = graph_helper.get_interface_graph(request, params)
        if get_interface_log is not None:
            # parse json to map
            dataDict = json.dumps(get_interface_log)
            jsonVal = json.loads(dataDict)
            for proxyLog in jsonVal:
                xData.append(proxyLog['createTime'])
                yData.append(proxyLog['successRate'])
    return render(request, 'interface/proxy.html',
                  {"after": after, "before": before, "flag": flag,
                   "name": name, "xData": json.dumps(xData), "yData": json.dumps(yData),
                   "csrf_token": get_token(request)})


def host_page(request):
    today = datetime.date.today()
    days = datetime.timedelta(days=7)
    days_before = today - days
    after = days_before.strftime('%Y-%m-%d')
    before = today.strftime('%Y-%m-%d')
    after = request.POST.get('after', after)
    before = request.POST.get('before', before)
    flag = request.POST.get('flag', 0)

    # search
    params = [('flag', flag), ('after', after), ('before', before)]

    # search
    count = 0
    xData = []
    proxyDict = {}
    # get all proxy
    get_ip_log = graph_helper.get_interface_host(request, params)
    if get_ip_log is not None:
        # parse json to map
        for key, val in get_ip_log.items():
            proxyData = []
            count += 1
            for i, el in enumerate(val):
                proxyData.append(el['successRate'])
                # add once ok
                if count == 1:
                    xData.append(el['createTime'])
            proxyDict[key] = proxyData
        proxyDict = json.dumps(proxyDict, encoding="UTF-8", ensure_ascii=False)
        logger.info(proxyDict)

    return render(request, 'interface/proxy_ip.html',
                  {"after": after, "before": before, "flag": flag, "xData": json.dumps(xData), "proxyDict": proxyDict,
                   "csrf_token": get_token(request)})


def get_interface(request):
    # return data for ajax calls
    proxy_list = graph_helper.get_interface_list(request)
    return HttpResponse(json.dumps({'proxy': proxy_list}), content_type="application/json")
