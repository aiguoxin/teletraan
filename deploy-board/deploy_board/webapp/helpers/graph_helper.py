__author__ = 'aiguoxin'

from deploy_board.webapp.helpers.deployclient import DeployClient

deployclient = DeployClient()

def get_proxy_by_id(request):
    params = [('ip', request.GET.get('ip'))]
    return deployclient.get("/proxylog/proxy", request.teletraan_user_id.token, params=params)


def get_proxy_log(request, params):
    return deployclient.post("/proxylog/graph", request.teletraan_user_id.token, params=params)

def get_ip_log(request, params):
    return deployclient.post("/proxylog/ip", request.teletraan_user_id.token, params=params)
