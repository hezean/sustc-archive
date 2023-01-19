import cyaron
import random


def next_subnet() -> list:
    subnet = f'{random.randint(1,255)}.{random.randint(1,255)}.{random.randint(1,255)}'
    return f'{subnet}.1/24', f'{subnet}.2/24'


def isrouter(idx: int) -> bool:
    global router_count
    return idx < router_count


if __name__ == '__main__':
    router_count = 10
    node_count = router_count*2
    weight_limit = 1000
    edges = []
    graph = cyaron.Graph.UDAG(router_count, router_count*3,
                              weight_limit=weight_limit, self_loop=False, repeated_edges=False)

    for e in graph.iterate_edges():
        edge = (e.start-1, e.end-1, e.weight)
        edges.append(edge)
    for router in range(router_count):
        host = router+router_count
        edge = (router, host, random.randint(1, weight_limit))
        edges.append(edge)
    ips = []
    nodes = [[] for _ in range(node_count)]
    ipedges = []
    for e in edges:
        u, v, w = e
        ipu, ipv = next_subnet()
        nodes[u].append(ipu)
        nodes[v].append(ipv)
        ipedges.append((ipu, ipv, w))
        ips.append(ipu)
        ips.append(ipv)

    ips_line = ''
    for ip in ips:
        ips_line += ip+' '

    routers_str = []
    routers_line = ''
    for idx in range(router_count):
        rstr = str(tuple(nodes[idx]))
        rstr = rstr.replace(' ', '')
        routers_str.append(rstr)
        routers_line += rstr+' '

    edges_line = ''
    for e in ipedges:
        estr = str(e)
        estr = estr.replace(' ', '')
        edges_line += estr+' '

    tests = []
    # for u in range(router_count, node_count-1):
        # tests.append(f'PATH {nodes[u][0]} {nodes[v][0]}')

    for idx in range(router_count):
        tests.append(f'TABLE {routers_str[idx]}')

    fname = '10router.txt'
    with open(fname, 'w+') as f:
        f.write(ips_line+'\n')
        f.write(routers_line+'\n')
        f.write(edges_line+'\n')
        f.write(str(len(tests))+'\n')
        for test in tests:
            f.write(test+'\n')
