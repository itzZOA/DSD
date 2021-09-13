import glob
import sys

from calculadora import Calculadora

from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

import logging

logging.basicConfig(level=logging.DEBUG)


class CalculadoraHandler:
    def __init__(self):
        self.log = {}

    def ping(self):
        print("me han hecho ping()")

    def mcd(self, x, y):
	while y != 0:
		temporal = y
		aux = x / y
		y = x - (aux * y)
		x = temporal
	return x

    def mcm(self, x, y):
	return (x * y) / self.mcd(x, y)
		

    def suma(self, n1, n2, n3, n4):
        print("sumando " + str(n1) + "/" + str(n2) + " con " + str(n3) + "/" + str(n4))
	m1 = self.mcm(n2, n4) / n2 * n1
	m2 = self.mcm(n2, n4) / n4 * n3
	m = m1 + m2
	d = self.mcm(n2, n4)
	resultado = float(m) / float(d)
	print("Fraccion Final: " + str(m) + "/" + str(d))
        return float(resultado)

    def resta(self, n1, n2, n3, n4):
        print("restando " + str(n1) + "/" + str(n2) + " con " + str(n3) + "/" + str(n4))
	m1 = mcm(n2, n4) / n2 * n1
	m2 = mcm(n2, n4) / n4 * n3
	m = m1 - m2
        d = self.mcm(n2, n4)
	resultado = float(m) / float(d)
	print("Fraccion Final: " + str(m) + "/" + str(d))
	return float(resultado)


    def multiplicacion(self, n1, n2, n3, n4):
        print("multiplicamos " + str(n1) + "/" + str(n2) + " con " + str(n3) + "/" + str(n4))
	numeradores = n1 * n3
	denominadores = n2 * n4
	resultado =  float(numeradores) / float(denominadores)
	print("Fraccion Final: " + str(numeradores) + "/" + str(denominadores))
        return float(resultado)

    def division(self, n1, n2, n3, n4):
        print("dividimos " + str(n1) + "/" + str(n2) + " entre " + str(n3) + "/" + str(n4))
	numeradores = n1 * n4
	denominadores = n2 * n3
	resultado =  float(numeradores) / float(denominadores)
	print("Fraccion Final: " + str(numeradores) + "/" + str(denominadores))
        return float(resultado)


if __name__ == "__main__":
    handler = CalculadoraHandler()
    processor = Calculadora.Processor(handler)
    transport = TSocket.TServerSocket(host="127.0.0.1", port=9090)
    tfactory = TTransport.TBufferedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()

    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

    print("iniciando servidor...")
    server.serve()
    print("fin")
