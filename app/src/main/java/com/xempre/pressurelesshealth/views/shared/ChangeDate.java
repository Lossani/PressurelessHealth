package com.xempre.pressurelesshealth.views.shared;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ChangeDate {
    public static ZonedDateTime change(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


        LocalDateTime fechaHora = LocalDateTime.parse(date, formatter);

// Obtener la zona horaria UTC-05
        ZoneId zonaHorariaUTCMinus5 = ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-5));

// Convertir la fecha y hora a UTC-05
        ZonedDateTime fechaHoraUTCMinus5 = fechaHora.atZone(zonaHorariaUTCMinus5);

// Obtener la zona horaria local del dispositivo
        ZoneId zonaHorariaLocal = ZoneId.systemDefault();

// Convertir la fecha y hora de UTC-05 a la zona horaria local del dispositivo
        ZonedDateTime fechaHoraLocal = fechaHoraUTCMinus5.withZoneSameInstant(zonaHorariaLocal);



        return fechaHoraLocal;
    }
}
