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
from django.conf.urls import url
import alarm_views
import capacity_views
import config_map_views
from deploy_board.webapp import interface_views
import env_config_views
import env_views
import metrics_views
import promote_views
import webhook_views
import deploy_views
import build_views
import host_views
import util_views
import security
import user_views
import docs_views
import graph_views
import code_views

urlpatterns = [
    # deploy related
    url(r'^deploy/inline_update/$', deploy_views.inline_update),
    url(r'^deploy/(?P<deploy_id>[a-zA-Z0-9\-_]+)', deploy_views.DeployView.as_view()),
    url(r'^deploys/ongoing/$', deploy_views.get_ongoing_deploys),
    url(r'^env/create/$', env_views.post_create_env),

    # envs related
    url(r'^envs/recent/$', env_views.get_recent_envs),
    url(r'^envs/names/$', env_views.get_env_names),
    url(r'^envs/deploys/$', env_views.get_all_deploys),
    url(r'^envs/search/(?P<filter>[a-zA-Z0-9\-_]+)/$', env_views.search_envs),
    url(r'^envs/search/$', env_views.EnvListView.as_view()),
    url(r'^envs/$', env_views.EnvListView.as_view()),

    # env related
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/add_stage/$', env_views.post_add_stage),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/get_users_config/$', user_views.get_users_config),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/update_users_config/$', user_views.update_users_config),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/get_users/$', user_views.get_users),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/get_user_token/(?P<user_name>[a-zA-Z0-9\-_]+)/$',
        user_views.get_user_token),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/deploy/$',
        env_views.EnvLandingView.as_view()),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/$',
        env_views.EnvLandingView.as_view()),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/$', env_views.EnvLandingView.as_view()),

    # stage related
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/new_deploy/$',
        env_views.EnvNewDeployView.as_view()),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/update_deploy_progress/$',
        env_views.update_deploy_progress),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/deploys/$',
        env_views.get_env_deploys),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/new_commits/$',
        env_views.get_new_commits),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/compare_deploys/$',
        env_views.compare_deploys),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/compare_deploys_2/$',
        env_views.compare_deploys_2),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/pred_deploys/$',
        env_views.get_pred_deploys),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/'
        r'(?P<buildId>[a-zA-Z0-9\-_]+)/warn_no_succ_deploy_in_pred/$',
        env_views.warn_no_succ_deploy_in_pred),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/builds/$',
        env_views.get_builds),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/groups/$',
        env_views.get_groups),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/restart/$', env_views.restart),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/pause/$', env_views.pause),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/resume/$', env_views.resume),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/rollback/$',
        env_views.rollback),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/rollback_to/'
        r'(?P<deploy_id>[a-zA-Z0-9\-_]+)', env_views.rollback_to),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/promote_to/'
        r'(?P<deploy_id>[a-zA-Z0-9\-_]+)$', env_views.promote_to),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/promote/'
        r'(?P<deploy_id>[a-zA-Z0-9\-_]+)$', env_views.promote),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/build/'
        r'(?P<build_id>[a-zA-Z0-9\-_]+)', env_views.deploy_build),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/commit/'
        r'(?P<commit>[a-zA-Z0-9\-_]+)', env_views.deploy_commit),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/deploy/'
        r'(?P<deploy_id>[a-zA-Z0-9\-_]+)', env_views.get_deploy),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/get_service_metrics/$',
        util_views.get_service_metrics),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/get_service_alarms/$',
        util_views.get_service_alarms),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/config_history/$',
        env_views.get_env_config_history),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/get_config_comparison/',
        env_views.get_config_comparison),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/show_config_comparison/',
        env_views.show_config_comparison),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/remove/$',
        env_views.remove_stage),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/hosts/$', env_views.get_hosts),

    # environment configs
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/config/$',
        env_config_views.EnvConfigView.as_view()),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/config/auto_deploy/$',
        promote_views.EnvPromoteConfigView.as_view()),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/config/capacity/$',
        capacity_views.EnvCapacityConfigView.as_view()),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/config/map/$',
        config_map_views.EnvConfigMapView.as_view()),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/config/alarm/$',
        alarm_views.EnvAlarmView.as_view()),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/config/metrics/$',
        metrics_views.EnvMetricsView.as_view()),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/config/webhooks/$',
        webhook_views.EnvWebhooksView.as_view()),

    # host related
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/hosts/unknowns/$',
        env_views.get_unknown_hosts),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/hosts/provision/$',
        env_views.get_provisioning_hosts),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/hosts/all/$',
        env_views.get_all_hosts),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/hosts/failed/$',
        env_views.get_failed_hosts),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/'
        r'(?P<deploy_id>[a-zA-Z0-9\-_]+)/hosts/$',
        env_views.get_hosts_by_deploy),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/reset_failed_hosts/'
        r'(?P<deploy_id>[a-zA-Z0-9\-_]+)/$', env_views.reset_failed_hosts),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/addinstance/$',
        env_views.add_instance),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/reset_deploy/'
        r'(?P<host_id>[a-zA-Z0-9\-_]+)/$', env_views.reset_deploy),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/pause_deploy/'
        r'(?P<host_id>[a-zA-Z0-9\-_]+)/$', env_views.pause_deploy),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/resume_deploy/'
        r'(?P<host_id>[a-zA-Z0-9\-_]+)/$', env_views.resume_deploy),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/host/(?P<hostname>[a-zA-Z0-9\-_]+)',
        host_views.HostDetailView.as_view()),
    url(r'^env/(?P<name>[a-zA-Z0-9\-_]+)/(?P<stage>[a-zA-Z0-9\-_]+)/terminate_host/(?P<hostname>[a-zA-Z0-9\-_]+)',
        host_views.terminate_host),
    url(r'^host/(?P<name>[a-zA-Z0-9\-_]+)/terminate/', host_views.terminate_instance),
    url(r'^host/(?P<name>[a-zA-Z0-9\-_]+)', host_views.get_host_details),
    url(r'^hosts/$', host_views.hosts_list),

    # builds related
    url(r'^builds/get_all_builds/$', build_views.get_all_builds),
    url(r'^builds/search_commit/(?P<commit>[a-zA-Z0-9\-_]+)/$', build_views.search_commit),
    url(r'^builds/names/(?P<name>[a-zA-Z0-9\-_]+)/builds/$', build_views.list_builds),
    url(r'^builds/names/(?P<name>[a-zA-Z0-9\-_]+)/branches/$',
        build_views.list_build_branches),
    url(r'^builds/names/$', build_views.get_build_names),
    url(r'^builds/(?P<id>[a-zA-Z0-9\-_]+)/$', build_views.get_build),
    url(r'^builds/$', build_views.builds_landing),

    # Commits related
    url(r'^commits/compare_commits/$', build_views.compare_commits),
    url(r'^commits/compare_commits_datatables/$', build_views.compare_commits_datatables),
    url(r'^commits/get_more_commits/$', build_views.get_more_commits),

    # metrics
    url(r'^get_site_health_metrics/$', util_views.get_site_health_metrics),
    url(r'^validate_metrics_url/$', util_views.validate_metrics_url),

    # mix
    url(r'^health_check/$', util_views.health_check),
    url(r'^auth/$', security.login_authorized),
    url(r'^api-docs/$', docs_views.SwaggerUIView.as_view()),
    url(r'^$', interface_views.host_page),

    # proxy graph
    url(r'^graph/$', graph_views.search_page),
    url(r'^graph/ip/$', graph_views.ip_page),
    url(r'^graph/proxy$', graph_views.get_proxy),

    # interface graph
    url(r'^interface/$', interface_views.search_page),
    url(r'^interface/host/$', interface_views.host_page),
    url(r'^interface/list$', interface_views.get_interface),

    # status code graph
    url(r'^code/$', code_views.index_page),
    url(r'^code/interface$', code_views.get_interface),

    ]
