struct inputs{
	float num1;
	float num2;
	char operador;
};

program CALCULADORA_PROG{
	version CALCULADORA_VER{
		float ADD(inputs)=1;
		float SUB(inputs)=2;
		float MUL(inputs)=3;
		float DIV(inputs)=4;
		float RES(inputs)=5;
		float POT(inputs)=6;
		
	}=1;
}=0x2fffffff;
