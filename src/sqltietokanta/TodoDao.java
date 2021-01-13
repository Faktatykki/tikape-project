
package sqltietokanta;

import java.sql.*;
import java.util.Random;

public class TodoDao {
    
    private Connection db;
    private Statement stmt;
    private ResultSet rs;
    private Random arpoja;
    
    public TodoDao() throws SQLException {
        luoTietokanta();
        this.rs = null;
        this.arpoja = new Random();
    }
    
    public void luoTietokanta() throws SQLException {
        
        this.db = DriverManager.getConnection("jdbc:sqlite:SQLTietokanta.db");
        this.stmt = db.createStatement();
        
        try {
           
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute("CREATE TABLE Paikat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE)");
            stmt.execute("CREATE TABLE Asiakkaat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE)");
            stmt.execute("CREATE TABLE Paketit (id INTEGER PRIMARY KEY, asiakas_id INTEGER REFERENCES Asiakkaat(id), seurantakoodi TEXT UNIQUE)");
            stmt.execute("CREATE TABLE Tapahtumat (id INTEGER PRIMARY KEY, seuranta_id INTEGER REFERENCES Paketit(id), paikka_id INTEGER REFERENCES Paikat(id), kuvaus TEXT, paivamaara DATETIME)");
            
            System.out.println("Tietokanta luotu");
            
        } catch(SQLException e) {
            System.out.println("Tietokanta on jo olemassa, luodaan yhteys");
        }
    }
    
    public void lisaaPaikka(String paikka) throws SQLException {  
        
        stmt.execute("BEGIN TRANSACTION");
        
        try {
           stmt.execute("INSERT INTO Paikat (nimi) VALUES ('" + paikka + "')");
            System.out.println("Paikka lisätty");
        } catch(SQLException e) {
            System.out.println("VIRHE: Paikka on jo olemassa");
        }
        
        stmt.execute("COMMIT");
    }
    
    public void lisaaAsiakas(String asiakas) throws SQLException {
        
        try {
            stmt.execute("INSERT INTO Asiakkaat (nimi) VALUES ('" + asiakas + "')");
            System.out.println("Asiakas lisätty");
        } catch (SQLException e) {
            System.out.println("VIRHE: Asiakas on jo olemassa");
        }
    }
    
    public void lisaaPaketti(String koodi, String asiakas) throws SQLException {
        
        boolean loytyi = false;
        int paketinVirhekoodi = 0;
        
        rs = stmt.executeQuery("SELECT seurantakoodi FROM Paketit");
        
        while(rs.next()) {
            if(rs.getString("seurantakoodi").equals(koodi)) {
                paketinVirhekoodi = 1;
            }
        }
        
        rs = stmt.executeQuery("SELECT nimi FROM Asiakkaat");
        
        while(rs.next()) {
            if(rs.getString("nimi").equals(asiakas)) {
                loytyi = true;
            }
        }
        
        if((loytyi) && paketinVirhekoodi == 0) {
            
            stmt.execute("BEGIN TRANSACTION");
            
            try {
                stmt.execute("INSERT INTO Paketit (asiakas_id, seurantakoodi) VALUES ((SELECT id FROM Asiakkaat WHERE nimi = '" + asiakas + "'), '" + koodi + "')");
                System.out.println("Paketti lisätty");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            
            stmt.execute("COMMIT");
            
        } else if(paketinVirhekoodi == 0){
            System.out.println("VIRHE: Asiakasta ei löydy");
        } else {
            System.out.println("VIRHE: Seurantakoodi on jo olemassa");
        }
    }
    
    public boolean lisaaTapahtuma(String sKoodi, String paikka, String kuvaus) throws SQLException {
        
        boolean loytyi = false;
        

        rs = stmt.executeQuery("SELECT seurantakoodi FROM Paketit");
        while(rs.next()) {
            if(rs.getString("seurantakoodi").equals(sKoodi)) {
                loytyi = true;
            }
        }
        
        if(!loytyi) {
            System.out.println("VIRHE: Pakettia ei löytynyt");
            return false;
        }
        
        loytyi = false;
        
        rs = stmt.executeQuery("SELECT nimi FROM Paikat");
        while(rs.next()) {
            if(rs.getString("nimi").equals(paikka)) {
                loytyi = true;
            }
        }
        
        if(!loytyi) {
            System.out.println("VIRHE: Paikkaa ei löytynyt");
            return false;
        }
        
        stmt.execute("BEGIN TRANSACTION");
        
        try {
            stmt.execute("INSERT INTO Tapahtumat (seuranta_id, paikka_id, kuvaus, paivamaara) VALUES ((SELECT id FROM Paketit WHERE seurantakoodi = '" + sKoodi + "'), (SELECT id FROM Paikat WHERE nimi = '" + paikka + "'), '" + kuvaus + "', CURRENT_TIMESTAMP)");
            System.out.println("Tapahtuma lisätty");
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        stmt.execute("COMMIT");
        
        return true;
    }
    
    public void haeTapahtumat(String seurantakoodi) throws SQLException {
        rs = stmt.executeQuery("SELECT DISTINCT T.paivamaara, P.nimi, T.kuvaus FROM Tapahtumat T, Paikat P, Paketit B WHERE T.paikka_id = P.id AND T.seuranta_id IN (SELECT id FROM Paketit WHERE seurantakoodi = '" + seurantakoodi + "')");
        while(rs.next()) {
            System.out.println(rs.getString("paivamaara") + ", " + rs.getString("nimi") + ", " + rs.getString("kuvaus"));
        }
    }
    
    public void haeAsiakkaanPaketit(String asiakas) throws SQLException {
        rs = stmt.executeQuery("SELECT DISTINCT P.seurantakoodi, COUNT(T.seuranta_id) AS maara FROM Paketit P LEFT JOIN Tapahtumat T ON P.id = T.seuranta_id WHERE P.asiakas_id IN(SELECT id FROM Asiakkaat WHERE nimi = '" + asiakas + "') GROUP BY P.seurantakoodi");
        while(rs.next()) {
            System.out.println(rs.getString("seurantakoodi") + ", " + rs.getInt("maara") + " tapahtumaa");
        }
    }
    
    public void haeTapahtumienMaara(String paikka, String pvm) throws SQLException {
        rs = stmt.executeQuery("SELECT COUNT(*) AS maara FROM Tapahtumat WHERE paikka_id IN(SELECT id FROM Paikat WHERE nimi = '" + paikka + "') AND (paivamaara >= '" + pvm + " 00:00:00 AM' AND paivamaara <= '" + pvm + " 23:59:59 PM')");
        while(rs.next()) {
            System.out.println("Tapahtumien määrä: " + rs.getInt("maara"));
        }
    }
    
    public void testaa() throws SQLException {       
        
        
        long aika1 = System.nanoTime();
        
        stmt.execute("BEGIN TRANSACTION");
        PreparedStatement paikat = db.prepareStatement("INSERT INTO Paikat (nimi) VALUES (?)");
        PreparedStatement asiakkaat = db.prepareStatement("INSERT INTO Asiakkaat (nimi) VALUES (?)");
        PreparedStatement paketit = db.prepareStatement ("INSERT INTO Paketit (asiakas_id, seurantakoodi) VALUES (?, ?)");
        PreparedStatement tapahtumat = db.prepareStatement ("INSERT INTO Tapahtumat (seuranta_id, paikka_id, kuvaus, paivamaara) VALUES (?, ?, ?, CURRENT_TIMESTAMP)");
        
        for(int i = 1; i <= 1000; i++) {
            try {
                paikat.setString(1, "P" + i);
                asiakkaat.setString(1, "A" + i);
                
                paketit.setInt(1, i);
                paketit.setString(2, "S" + i);
                
                paikat.addBatch();
                asiakkaat.addBatch();
                paketit.addBatch();
                
                if(i % 250 == 0) {
                    paikat.executeBatch();
                    asiakkaat.executeBatch();
                    paketit.executeBatch();
                    
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        
        for(int j = 1; j <= 1000000; j++) {
            try{
                int luku = arpoja.nextInt(1000) + 1;
                tapahtumat.setInt(1, luku);
                tapahtumat.setInt(2, luku);
                tapahtumat.setString(3, String.valueOf(luku));

                tapahtumat.addBatch();

                if(j % 250 == 0) {
                    tapahtumat.executeBatch();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
       
        stmt.execute("COMMIT");
        long aika2 = System.nanoTime();
        
        System.out.println("Aikaa kului " + (aika1 - aika2)/1e9 + " sekuntia");
    }
    
    public void testaaEtsiPakettienMaara(int indeksi) throws SQLException {
        
        long aika1 = System.nanoTime();
        
        int luku;
        
        if(indeksi == 1) {
            try {
                stmt.execute("CREATE INDEX idx_asiakasId ON Paketit (asiakas_id)");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        
        if(indeksi == 2) {
            try {
                stmt.execute("DROP INDEX idx_asiakasId");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        
        try {
            for(int i = 1; i <= 1000; i++) {
                luku = arpoja.nextInt(1000) + 1;
                rs = stmt.executeQuery("SELECT COUNT(id) as maara FROM Paketit WHERE asiakas_id = " + luku);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        long aika2 = System.nanoTime();
        System.out.println("Aikaa kului " + (aika1 - aika2)/1e9 + " sekuntia");
    }
    
    public void testaaEtsiPakettienTapahtumat(int indeksi) throws SQLException {
        
        long aika1 = System.nanoTime();
        
        int luku;
       
        if(indeksi == 1) {
            try {
                stmt.execute("CREATE INDEX idx_seurantaId ON Tapahtumat (seuranta_id)");
            } catch (SQLException e) {
            }
        }
        
        if(indeksi == 2) {
            try {
                stmt.execute("DROP INDEX idx_seurantaId");
            } catch (SQLException e) {
            }
        }
        
        try {
            for(int i = 1; i <= 1000; i++) {
                luku = arpoja.nextInt(1000) + 1;
                rs = stmt.executeQuery("SELECT COUNT(id) AS tapahtumaa FROM Tapahtumat WHERE seuranta_id = " + luku);
            }
        } catch (SQLException e) {
        }
        
        long aika2 = System.nanoTime();
        System.out.println("Aikaa kului " + (aika1 - aika2)/1e9 + " sekuntia");
    }
}