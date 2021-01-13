
package sqltietokanta;

import  java.sql.*;
import java.util.Scanner;

public class SQLTietokanta {

    public static void main(String[] args) throws SQLException {
        Scanner lukija = new Scanner(System.in);
        Kayttoliittyma kayttoliittyma = new Kayttoliittyma(lukija);
        
        kayttoliittyma.kaynnista();
    }
}
