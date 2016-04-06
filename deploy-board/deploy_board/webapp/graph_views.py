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
from django.middleware.csrf import get_token

from django.shortcuts import render
from django.http import HttpResponse
import time
from deploy_board.webapp import common
from deploy_board.webapp.helpers import graph_helper
logger = logging.getLogger(__name__)


def date_to_timestamp(date):
    result = time.strptime(date, '%Y-%m-%d')
    return int(time.mktime(result))

def timestamp_to_date(timestamp):
    if timestamp > 10000000000:
        timestamp /= 1000
    x = time.localtime(timestamp)
    return time.strftime('%Y-%m-%d %H:%M:%S', x)


def search_page(request):
    # todo get host
    after = "2016-04-06"
    before = "2016-04-07"
    after = request.POST.get('after', after)
    before = request.POST.get('before', before)
    ip = request.POST.get('ip', None)
    name = request.POST.get('name', None)

    # search
    xData = []
    yData = []
    params = [('ip', ip), ('after', date_to_timestamp(after)), ('before', date_to_timestamp(before)), ('name', name)]
    if ip is not None and name is not None:
        get_proxy_log = graph_helper.get_proxy_log(request, params)
        if get_proxy_log is not None:
            get_proxy_log = common.byteify(get_proxy_log)
            # parse json to map
            logger.info(get_proxy_log)
            dataDict = json.dumps(get_proxy_log)
            jsonVal = json.loads(dataDict)
            for proxyLog in jsonVal:
                xData.append(timestamp_to_date(proxyLog['createTime']))
                yData.append(proxyLog['successRate'])
    logger.info(xData)
    logger.info(yData)

    return render(request, 'graph/proxy.html',
                  {"after": after, "before": before, "ip": ip,
                   "name": name, "xData": json.dumps(xData), "yData": json.dumps(yData),
                   "csrf_token": get_token(request)})


def get_proxy(request):
    # return data for ajax calls
    proxy_list = graph_helper.get_proxy_by_id(request)
    return HttpResponse(json.dumps({'proxy': proxy_list}), content_type="application/json")
