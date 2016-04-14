__author__ = 'aiguoxin'

from deploy_board.webapp.helpers.deployclient import DeployClient

deployclient = DeployClient()

# for proxy graph

def get_proxy_by_id(request):
    params = [('ip', request.GET.get('ip'))]
    return deployclient.get("/proxylog/proxy", request.teletraan_user_id.token, params=params)


def get_proxy_log(request, params):
    return deployclient.post("/proxylog/graph", request.teletraan_user_id.token, params=params)

def get_ip_log(request, params):
    return deployclient.post("/proxylog/ip", request.teletraan_user_id.token, params=params)

# for interface graph

def get_interface_graph(request, params):
    return deployclient.post("/interface/graph", request.teletraan_user_id.token, params=params)

def get_interface_host(request, params):
    return deployclient.post("/interface/host", request.teletraan_user_id.token, params=params)

def get_interface_list(request):
    params = [('flag', request.GET.get('flag'))]
    return deployclient.get("/interface/list", request.teletraan_user_id.token, params=params)

# for status code graph

def get_ip_interface_list(request):
    params = [('ip', request.GET.get('ip'))]
    return deployclient.get("/code/interface", request.teletraan_user_id.token, params=params)

def get_line_graph_data(request, params):
    return deployclient.post("/code/line", request.teletraan_user_id.token, params=params)

def get_pie_graph_data(request, params):
    return deployclient.post("/code/pie", request.teletraan_user_id.token, params=params)
