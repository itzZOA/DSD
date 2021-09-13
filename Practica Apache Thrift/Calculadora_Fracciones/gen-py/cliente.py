from calculadora import Calculadora

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

transport = TSocket.TSocket("localhost", 9090)
transport = TTransport.TBufferedTransport(transport)
protocol = TBinaryProtocol.TBinaryProtocol(transport)

client = Calculadora.Client(protocol)

transport.open()

print("hacemos ping al server")
client.ping()

print(" \n*****CALCULADORA FRACCIONES***** \nRecuerda \n 1.Sumar \n 2.Restar \n 3.Multiplicar \n 4.Dividir \n")
print("Ingresa el numerador de la primera fraccion")
nu1 = float(input())
print("Ingresa el denominador de la primera fraccion")
nu2 = float(input())
print("Ingresa el numerador de la segunda fraccion")
nu3 = float(input())
print("Ingresa el denominador de la segunda fraccion")
nu4 = float(input())
print("Ingresa operador")
op = int(input())

if (op==1):
	resultado = client.suma(nu1, nu2, nu3, nu4)
	print(" \nSeleccionaste 1.Sumar \n " + str(nu1) + "/" + str(nu2) + " + " + str(nu3) + "/" + str(nu4) + " = " + str(resultado))
elif (op==2):
	resultado = client.resta(nu1, nu2, nu3, nu4)
	print(" \nSeleccionaste 2.Restar \n " + str(nu1) + "/" + str(nu2) + " - " + str(nu3) + "/" + str(nu4) + " = " + str(resultado))
elif (op==3):
	resultado = client.multiplicacion(nu1, nu2, nu3, nu4)
	print(" \nSeleccionaste 3.Multiplicar \n "+ str(nu1) + "/" + str(nu2) + " * " + str(nu3) + "/" + str(nu4) + " = " + str(resultado))
elif (op==4):
	resultado = client.division(nu1, nu2, nu3, nu4)
	print(" \nSeleccionaste 4.Dividir \n " + str(nu1) + "/" + str(nu2) + " / " + str(nu3) + "/" + str(nu4) + " = " + str(resultado))

transport.close()
