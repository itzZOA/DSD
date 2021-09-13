/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#ifndef _CALCULADORA_H_RPCGEN
#define _CALCULADORA_H_RPCGEN

#include <rpc/rpc.h>


#ifdef __cplusplus
extern "C" {
#endif


struct inputs {
	float num1;
	float num2;
	char operador;
};
typedef struct inputs inputs;

#define CALCULADORA_PROG 0x2fffffff
#define CALCULADORA_VER 1

#if defined(__STDC__) || defined(__cplusplus)
#define ADD 1
extern  float * add_1(inputs *, CLIENT *);
extern  float * add_1_svc(inputs *, struct svc_req *);
#define SUB 2
extern  float * sub_1(inputs *, CLIENT *);
extern  float * sub_1_svc(inputs *, struct svc_req *);
#define MUL 3
extern  float * mul_1(inputs *, CLIENT *);
extern  float * mul_1_svc(inputs *, struct svc_req *);
#define DIV 4
extern  float * div_1(inputs *, CLIENT *);
extern  float * div_1_svc(inputs *, struct svc_req *);
#define RES 5
extern  float * res_1(inputs *, CLIENT *);
extern  float * res_1_svc(inputs *, struct svc_req *);
#define POT 6
extern  float * pot_1(inputs *, CLIENT *);
extern  float * pot_1_svc(inputs *, struct svc_req *);
extern int calculadora_prog_1_freeresult (SVCXPRT *, xdrproc_t, caddr_t);

#else /* K&R C */
#define ADD 1
extern  float * add_1();
extern  float * add_1_svc();
#define SUB 2
extern  float * sub_1();
extern  float * sub_1_svc();
#define MUL 3
extern  float * mul_1();
extern  float * mul_1_svc();
#define DIV 4
extern  float * div_1();
extern  float * div_1_svc();
#define RES 5
extern  float * res_1();
extern  float * res_1_svc();
#define POT 6
extern  float * pot_1();
extern  float * pot_1_svc();
extern int calculadora_prog_1_freeresult ();
#endif /* K&R C */

/* the xdr functions */

#if defined(__STDC__) || defined(__cplusplus)
extern  bool_t xdr_inputs (XDR *, inputs*);

#else /* K&R C */
extern bool_t xdr_inputs ();

#endif /* K&R C */

#ifdef __cplusplus
}
#endif

#endif /* !_CALCULADORA_H_RPCGEN */
