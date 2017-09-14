package com.dataservicios.ttauditotrosagentes.util;
/**
 * Created by usuario on 11/11/2014.
 */
public final class GlobalConstant {
    //public static String dominio = "http://appfiliaibk.com";
   public static String dominio = "http://ttaudit.com";
   // public static String dominio = "http://192.168.1.40/ttaudit.com/backend/ttaudit1/public";
    //public static String dominio = "http://192.168.1.40/ttaudit.com/backend/ttaudit1/public";
    //public static String dominio = "http://dataservicios.com/ttaudit";
    //Testeo Local:
    //private static final String LOGIN_URL = "http://192.168.1.45/webservice/login.php";
    //Testeo real server:
    //loginUser
    //public static final String LOGIN_URL = dominio + "/webservice/login.php" ;
    public static final String LOGIN_URL = dominio + "/loginUser" ;
    public static final String KEY_USERNAME = "username";
    public static String inicio,fin;
    public static  double latitude_open, longitude_open;
    public static  int global_close_audit =0;
    public static int company_id = 102;
    public static String type_aplication = "android";

    public static int[] poll_id = new int[]{
       1773 ,//0    1703,1703 // 1635,   //  0 Al llegar al establecimiento el cliente incógnito deberá preguntar directamente por el agente de Interbank. Ejemplo: Buenos días/tardes, ¿hay agente de Interbank aquí?
       1774 ,//1    1704,1704 // 1636,   //  1 Indicar Rubro
       1775 ,//2    1705,1705 // 1637,   //  2 ¿Se encuentra abierto el agente?
       1776 ,//3    1706,1706 // 1638,   //  3 ¿El letrero de IBK Agente era visible desde fuera?
       1777 ,//4    1707,1707 // 1639,   //  4 ¿El Interbank Agente es visible estando dentro del establecimiento?
       1778 ,//5    1708,1708 // 1640,   //  5 ¿Existe algún otro Agente / corresponsal bancario?
       1779 ,//6    1709,1709 // 1641,   //  6 El CI deberá preguntar,  ¿Puedo pagar una tarjeta de crédito de Interbank acá?
       1780 ,//7    1710,1710 // 1642,   //  7 En el caso de que exista más de un agente en el comercio, preguntar, ¿acá puedo pagar mi teléfono?
       1781 ,//8    1711,1711 // 1643,   //  8 Si responde que si en la P8, preguntar ¿Y en cuál agente me conviene pagar mi teléfono?
       1782 ,//9    1712,1712 // 1644,   //  9 Si me envían dinero del exterior ¿Lo puedo cobrar acá?
       1783 ,//10    1713,1713 // 1645,   //  10 Al preguntar si se podía hacer la operación correspondiente, ¿el dependiente aceptó realizar la operación?
       1784 ,//11    1714,1714 // 1646,   //  11 ¿Su solicitud fue atendido de inmediato?
       1785 ,//12    1715,1715 // 1647,   //  12  Su solicitud no fue atendida de inmediato porque
       1786 ,//13    1716,1716 // 1648,   //  13 Mientras esperaba. ¿La persona que lo atendió se preocupó por su tiempo?
       1787 ,//14    1717,1717 // 1649,   //  14 Después de esperar
       1788 ,//15    1718,1718 // 1650,   //  15 ¿La transacción se llegó a realizar de manera exitosa?  (Se considera exitosa cuando se entrega el voucher)
       1789 ,//16    1719,1719 // 1651,   //  16 ¿Cuántos MINUTOS transcurrieron entre que solicitó la transacción y la persona terminó (le entregá el voucher)?
       1790 ,//17    1720,1720 // 1652,   //  17 ¿La persona que lo atendió tuvo que solicitar ayuda de alguna otra persona o hacer alguna consulta al respecto?
       1791 ,//18    1721,1721 // 1653,   //  18 ¿Le entregaron ESPONTÁNEAMENTE un comprobante luego de la transacción? (Si no le entregaron espontáneamente el voucher deben solicitarlo y adjuntarlo al formulario)
       1792 ,//19    1722,1722 // 1654,   //  19 (SÓLO SI NO SE REALIZÓ LA TRANSACCIÓN) ¿Por qué no se pudo realizar la transacción?
       1793 ,//20    1723,1723 // 1655,   //  20 (SÓLO SI NO SE REALIZÓ LA TRANSACCIÓN) ¿Le dieron alguna solución para poder realizar la transacción?
       1794 ,//21    1724,1724 // 1656,   //  21 Escoger tipo de Transacción
       1795 ,//22    1725,1725 // 1657,   //  22 ¿El agente hizo algún cobro fuera del voucher?
       1796 ,//23    1726,1726 // 1658,   //  23 En una escala del 0 al 3 donde 0 significa Debajo del estándar, 2 Estándar y 3 Superior, ¿cómo calificarías la amabilidad de la persona que te atendió?
       1797 ,//24    1727,1727 // 1659,   //  24 En una escala del 0 al 3 donde 0 significa Debajo del estándar, 2 Estándar y 3 Superior, ¿cómo calificarías el conocimiento de la persona que lo atendió?
       1798 ,//25    1728,1728 // 1660,   //  25 En una escala del 0 al 3 donde 0 significa Debajo del estándar, 2 Estándar y 3 Superior, ¿cómo calificarías la disposición de la persona que lo atendió?
       1799 ,//26    1729,1729 // 1661,   //  26 El CI deberá mostrar interés: Voy a abrir un negocio, ¿usted me recomendaría tener un agente Interbank?
       1800 ,//27    1730,1730 // 1662,   //  27 Otras apreciaciones a comentar
       1801 ,//28    1769,1769	//           37 ¿Tus comisiones te las pagan puntualmente?
       1802 ,//29    1770,1770	//           38 ¿Me puedes referir a tu ejecutivo IBK?
       1803 ,//30    1772,1772	//           39 ¿Existen ofertas para adelanto de sueldo o prestamos a travez de sus agentes?
} ;


}
