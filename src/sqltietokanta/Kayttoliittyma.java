
package sqltietokanta;

import java.sql.*;
import java.util.Scanner;

public class Kayttoliittyma {
    
    private Scanner lukija;
    private TodoDao tietokanta;
    
    public Kayttoliittyma(Scanner lukija) throws SQLException {
        this.lukija = lukija;
        this.tietokanta = null;
        
    }
    
    public void kaynnista() throws SQLException {
        while(true) {
            if(tulostaAlkuvalikkoJaLuoYhteys()) {
                break;
            }
        }
        tulostaValikko();
        valitseToiminto();
    }
    
    public void valitseToiminto() throws SQLException {
        
        while(true) {
            
            System.out.println("");
            System.out.print("Valitse toiminto (1-9): ");
            
            int toiminto = Integer.valueOf(lukija.nextLine());
            
            if(toiminto == 0) {
                break;
            }
            if(toiminto == 1) {
                lisaaPaikka();
            }
            if(toiminto == 2) {
               lisaaAsiakas();
            }
            if(toiminto == 3) {
               lisaaPaketti();
            }
            if(toiminto == 4) {        
                lisaaTapahtuma();
            }
            if(toiminto == 5) {
                haePaketinTapahtumat();
            }
            if(toiminto == 6) {
                haeAsiakkaanPaketit();
            }
            if(toiminto == 7) {
                haeTapahtumienMaaraPaivamaaralla();
            }
            if(toiminto == 8) {
               rasitustesti();
            }
        }
    }
    
    public boolean tulostaAlkuvalikkoJaLuoYhteys() throws SQLException {
        System.out.println("");
        System.out.println("1. Luo sovelluksen tarvitsemat taulut tyhjään tietokantaan.");
        System.out.println("   Jos taulut ovat jo luotu, luodaan yhteys tietokantaan.");
        System.out.println("");
        System.out.print("Valitse toiminto: ");
        
        int komento = Integer.valueOf(lukija.nextLine());
        
        if(komento == 1) {
            this.tietokanta = new TodoDao();
            return true;
            
        } else {
            System.out.println("Paina vaan sitä ykköstä.");
        }
        
        return false;
    }
    
    public void tulostaValikko() {
        System.out.println("");
        System.out.println("1. Lisää uusi paikka tietokantaan.");
        System.out.println("2. Lisää uusi asiakas tietokantaan.");
        System.out.println("3. Lisää uusi paketti tietokantaan.");
        System.out.println("4. Lisää uusi tapahtuma tietokantaan.");
        System.out.println("5. Hae paketin tapahtumat.");
        System.out.println("6. Hae kaikki asiakkaan paketit ja niihin liittyvät tapahtumat.");
        System.out.println("7. Hae annetusta paikasta tapahtumien määrä tiettynä päivänä.");
        System.out.println("8. Rasitustestien valinta.");
        System.out.println("0. Lopeta ohjelma");
    }
    
    public void lisaaPaikka() throws SQLException {
        System.out.print("Anna paikan nimi: ");
        String paikka = lukija.nextLine();
        
        tietokanta.lisaaPaikka(paikka);
    }
    
    public void lisaaAsiakas() throws SQLException {
        System.out.print("Anna asiakkaan nimi: ");
        String asiakas = lukija.nextLine();
       
        tietokanta.lisaaAsiakas(asiakas);
    }
    
    public void lisaaPaketti() throws SQLException {
        System.out.print("Anna paketin seurantakoodi: ");
        String koodi = lukija.nextLine();
        System.out.print("Anna asiakkaan nimi: ");
        String asiakas = lukija.nextLine();
       
        tietokanta.lisaaPaketti(koodi, asiakas);
    }
    
    public void lisaaTapahtuma() throws SQLException {
        System.out.print("Anna paketin seurantakoodi: ");
        String sKoodi = lukija.nextLine();
        System.out.print("Anna tapahtuman paikka: ");
        String paikka = lukija.nextLine();
        System.out.print("Anna tapahtuman kuvaus: ");
        String kuvaus = lukija.nextLine();
        
        tietokanta.lisaaTapahtuma(sKoodi, paikka, kuvaus);
    }
    
    public void haePaketinTapahtumat() throws SQLException {
        System.out.print("Anna paketin seurantakoodi: ");
        String sKoodi = lukija.nextLine();
        
        tietokanta.haeTapahtumat(sKoodi);
    }
    
    public void haeAsiakkaanPaketit() throws SQLException {
        System.out.print("Anna asiakkaan nimi: ");
        String asiakas = lukija.nextLine();
        
        tietokanta.haeAsiakkaanPaketit(asiakas);
    }
    
    public void haeTapahtumienMaaraPaivamaaralla() throws SQLException {
        System.out.print("Anna paikan nimi: ");
        String paikka = lukija.nextLine();
        System.out.print("Anna päivämäärä (muodossa YYYY-MM-DD): ");
        String pvm = lukija.nextLine();
        
        tietokanta.haeTapahtumienMaara(paikka, pvm);
    }
    
    public void rasitustesti() throws SQLException {
        System.out.println("");
        System.out.println("1. Luo 1000 asiakasta, 1000 paikkaa ja 1000000 tapahtumaa.");
        System.out.println("2. Suorita tuhat kyselyä (paketit).");
        System.out.println("3. Suorita tuhat kyselyä (tapahtumat).");
        System.out.println("0. Poistu testivalikosta.");
        System.out.println("");
        
        while(true) {
            System.out.print("Valitse testi: ");
            int komento = Integer.valueOf(lukija.nextLine());
            
            if(komento == 0) {
                tulostaValikko();
                break;
            }
            if(komento == 1) {
                tietokanta.testaa();
            }
            if(komento == 2) {
                System.out.println("");
                System.out.println("1. Indeksillä.");
                System.out.println("2. Ilman indeksiä.");
                System.out.print("Komento: ");
                int indeksiVastaus = Integer.valueOf(lukija.nextLine());

                if((indeksiVastaus == 1 || indeksiVastaus == 2)) {             
                    tietokanta.testaaEtsiPakettienMaara(indeksiVastaus);
                } else {
                    System.out.println("Aloitappa alusta");
                }
            }
            if(komento == 3) {
                System.out.println("");
                System.out.println("1. Indeksillä.");
                System.out.println("2. Ilman indeksiä.");
                System.out.print("Komento: ");
                int vastausIndeksi = Integer.valueOf(lukija.nextLine());

                if((vastausIndeksi == 1 || vastausIndeksi == 2)) {
                        tietokanta.testaaEtsiPakettienMaara(vastausIndeksi);
                } else {
                    System.out.println("Aloitappa alusta");
                }
            }
        }
    }
}
