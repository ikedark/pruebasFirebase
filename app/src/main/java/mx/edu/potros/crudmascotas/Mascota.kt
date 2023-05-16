package mx.edu.potros.crudmascotas

data class Mascota(var mascotaId : String? = null,
                   var nombreM : String? = null,
                   var descM : String?= null,
                   var fecha : String?= null,
                   var celDuenio : String?= null,
                   var ubicacion: String?= null,
                   var img: String?= "",
                   var sexo: String?= null)
