struct inputs{
	float num1;
	float num2;
	float num3;
	float num4;
	char operador;
};

program CALCULADORA_PROG{
	version CALCULADORA_VER{
		float ADD(inputs)=1;
		float SUB(inputs)=2;
		float MUL(inputs)=3;
		float DIV(inputs)=4;
		
	}=1;
}=0x2fffffff;
