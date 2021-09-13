//Creamos las constantes y variables
const http = require ('http');
const url = require ('url');
const fs = require ('fs');
const path = require ('path');
const socketio = require ('socket.io');
var MongoClient = require('mongodb').MongoClient;
var MongoServer = require('mongodb').Server;


//Funcion para guardarla  fecha en el formato que deseamos
function getTimeStamp(){
  var date       = new Date();
  var dia        = ModificarFecha(date.getDate());
  var mes        = ModificarFecha(date.getMonth());
  var fecha      = dia + "/" + mes + "/" + date.getFullYear();
  var horas      = ModificarFecha(date.getHours());
  var minutos    = ModificarFecha(date.getMinutes());
  var segundos   = ModificarFecha(date.getSeconds());
  var hora       = horas + ":" + minutos + ":" + segundos;
  date           = fecha + " - " + hora;

  return date; //Nos devolvera  por ejemplo 17/04/2021 - 23:39
}

function getHour(){
	var date = new Date();
	var hora = date.getHours();
return hora;

}

//Al hacer lo anterior puede ser que se tenga solo una cifra por tanto es funcion añade un 0 antes en ese dato.
function ModificarFecha(date){
  var nueva = date;
  if(date<10){
    nueva = "0"+date;
  }
  return nueva ;
}

//Funcion que añade los ficheros html  y css
var httpServer = http.createServer (
  function (request, response) {

    switch(request.url){
      case "/estilo.css":
        response.writeHead(200, {"Content-Type": "text/css"});
        var file = path.join(process.cwd(), "/estilo.css");
	fs.exists (file, function (exists) {
    		if(exists){
      			fs.readFile (file, function (err, data) {
        			if (!err)
          				response.end(data);
      			});
    		}
  	});
        break;

      default:
        response.writeHead(200, {"Content-Type": "text/html"});
         var file = path.join(process.cwd(), "/servidor.html");
	fs.exists (file, function (exists) {
    		if(exists){
      			fs.readFile (file, function (err, data) {
        			if (!err)
          				response.end(data);
      			});
    		}
  	});
        break;
    }

  }
);


/**
 * Servidor MongoDB junto con las conexiones por parte de los usuarios
 *
 */

 MongoClient.connect("mongodb://localhost:27017/domotica", { useNewUrlParser: true }, function(err, db) {
 	if(err)
 		throw err;

  var bdatos = db.db("domotica");
  var mensaje = null;
  var puerto = 8080;
 
  console.log("Servidor listo en puerto: " + puerto);
	var io = socketio(httpServer);

  bdatos.createCollection("luminosidad", function(err, collection){
		if(!err){
			console.log("Colección creada en Mongo: " + collection.collectionName);
		}
	});

  bdatos.createCollection("temperatura", function(err, collection){
		if(!err){
			console.log("Colección creada en Mongo: " + collection.collectionName);
		}
	});

  io.sockets.on('connection',function(socket) {
    console.log("Conectado");

    /*** Inserción de colección ***/
      /** Valores **/
    socket.on('enviar-datos-temperatura', function (datos) {
			bdatos.collection("temperatura").insert({valor:datos[0], minimo:datos[1], maximo:datos[2]},
       {safe:true}, function(err, result) {
        if (!err) {
          console.log("Insertado en Temperaturas: {valor:"+datos[0]+", minimo:"+datos[1]+", maximo:"+datos[2]+"}");
          io.sockets.emit('log', getTimeStamp() + " - Modificados valores de temperatura");
        }
				else
					console.log("Error al insertar datos en la colección.");
			});
		});
		
      /** Estado  **/
    socket.on('slider_1', function (datos) {
      bdatos.collection("temperaturas").insert({estado:datos}, {safe:true}, function(err, result) {
        if (!err) {
          console.log("Insertado en Temperaturas: {estado:"+datos+"}");
          io.sockets.emit('log', getTimeStamp() + " - Aire Acondicionado " + datos);
        }
        else
          console.log("Error al insertar datos en la colección.");
      });
    });
    
      /** Estado  **/
    socket.on('slider_3', function (datos) {
      bdatos.collection("temperaturas").insert({estado:datos}, {safe:true}, function(err, result) {
        if (!err) {
          console.log("Insertado en Temperaturas: {estado:"+datos+"}");
          io.sockets.emit('log', getTimeStamp() + " - Calefación " + datos);
        }
        else
          console.log("Error al insertar datos en la colección.");
      });
    });


    /*** Inserción de colección Luminosidad ***/
      /** Valores **/
		socket.on('enviar-datos-luminosidad', function (datos) {
			bdatos.collection("luminosidad").insert({valor:datos[0], minimo:datos[1], maximo:datos[2]},
       {safe:true}, function(err, result) {
        if (!err) {
          console.log("Insertado en Luminosidad : {valor:"+datos[0]+", minimo:"+datos[1]+", maximo:"+datos[2]+"}");
          io.sockets.emit('log', getTimeStamp() + " - Modificados valores de luminosidad");
        }
				else
					console.log("Error al insertar datos en la colección.");
			});
		});
		
      /** Estado Persianas**/
    socket.on('slider_2', function (datos) {
			bdatos.collection("luminosidad").insert({estado:datos}, {safe:true}, function(err, result) {
        if (!err) {
          console.log("Insertado en Luminosidad: {estado:"+datos+"}");
          io.sockets.emit('log', getTimeStamp() + " - Persianas " + datos);
        }
				else
					console.log("Error al insertar datos en la colección.");
			});
		});
	
	  /** Estado Luces **/
    socket.on('slider_4', function (datos) {
			bdatos.collection("luminosidad").insert({estado:datos}, {safe:true}, function(err, result) {
        if (!err) {
          console.log("Insertado en Luminosidad: {estado:"+datos+"}");
          io.sockets.emit('log', getTimeStamp() + " - Luces " + datos);
        }
				else
					console.log("Error al insertar datos en la colección.");
			});
		});

    /*** Avisos ***/
    socket.on ('aviso', function (sensor) {
      var log = "Aviso: " + sensor + " fuera de umbral";
      console.log (log);
      io.sockets.emit ('aviso', log);
    });
    
    socket.on ('accion', function () {
      var log = "Aviso: los sensores exceden los umbrales máximos";
      console.log (log);
      io.sockets.emit ('aviso', log);
    })

    socket.on ('tarifa', function () {
    	var hora = getHour();
    	if ((hora>=0) && (hora<8))
      		var log = "Atención: Estas en la franja horaria más barata";
      	else if (((hora>=8) && (hora<10)) || ((hora>=14) && (hora<18)) || ((hora>=22) && (hora<=23)))
      		var log = "Atención: Estas en la franja horaria intermedia";
      	else
      		var log = "Atención: Estas en la franja horaria más cara";
      console.log (log);
      io.sockets.emit ('tarifa', log);
    })

  });
});

 httpServer.listen(8080);
 console.log("Servicio domótico iniciado con éxito");
