package com.company;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class Okno extends JFrame {
    // dane do nawiązania komunikacji z bazą danych
    private String jdbcUrl = "jdbc:mysql://localhost:3306/ksiegarnia?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", jdbcUser = "root", jdbcPass = "1234";
    // pole na komunikaty od aplikacji
    private JTextField komunikat = new JTextField();
    // panel z zakładkami
    private JTabbedPane tp = new JTabbedPane();
    private JPanel p_kli = new JPanel(); // klienci
    private JPanel p_ksi = new JPanel(); // ksiązki
    private JPanel p_zam = new JPanel(); // zamówiemia
    // panel dla zarządzania klientami
    private JTextField pole_pesel = new JTextField();
    private JTextField pole_im = new JTextField();
    private JTextField pole_naz = new JTextField();
    private JTextField pole_mail = new JTextField();
    private JTextField pole_adr = new JTextField();
    private JTextField pole_tel = new JTextField();
    private JButton przyc_zapisz_kli = new JButton("zapisz klienta");
    private JButton przyc_usun_kli = new JButton("usuń klienta");
    private DefaultListModel<String> lmodel_kli = new DefaultListModel<>();
    private JList<String> l_kli = new JList<>(lmodel_kli);
    private JScrollPane sp_kli = new JScrollPane(l_kli);
    //panel dla zarządzania ksiazkami
    private JTextField pole_isbn = new JTextField();
    private JTextField pole_author = new JTextField();
    private JTextField pole_title = new JTextField();
    String[] items = {"sensacja", "kryminał", "thriller", "horror", "obyczajowa", "poradnik", "biografia", "historiczna", "romans", "popularnonaukowa", "młodzieżowa", "dziecięca", "reportaż", "podręcznik"};
    private JComboBox pole_type;
    private JTextField pole_publisher = new JTextField();
    private JTextField pole_price = new JTextField();
    private JButton przyc_zapisz_ksi = new JButton("zapisz ksiazke");
    private JButton przyc_usun_ksi = new JButton("usuń ksiazke");
    private JButton przyc_zmien_cena_ksi = new JButton("zmien cene ksiazki");
    private JButton przyc_zapisz_zmiane_ksi = new JButton("zapisz zmiane w cenie ksiazki");
    private DefaultListModel<String> lmodel_ksi = new DefaultListModel<>();
    private JList<String> l_ksi = new JList<>(lmodel_ksi);
    private JScrollPane sp_ksi = new JScrollPane(l_ksi);
    //panel dla zarządzania zamowieniami
    private String[] clientList;
    private JComboBox pole_clients;
    private String[] bookList;
    private JComboBox pole_books;
    String[] status = {"oczekuje", "wysłane", "zaplacone"};
    private JComboBox pole_status;
    private JButton przyc_zmien_status_zam = new JButton("zmien status");
    private JButton przyc_zatwierdz_zam = new JButton("zatwierdz zmiane statusu");
    private JButton przyc_zapisz_zam = new JButton("zapisz zamowienie");
    private DefaultListModel<String> lmodel_zam = new DefaultListModel<>();
    private JList<String> l_zam = new JList<>(lmodel_zam);
    private JScrollPane sp_zam = new JScrollPane(l_zam);
    Date todaysDate = new Date();
    private JSpinner spinner_zam;
    private JSpinner spinner_kli;
    private JSpinner spinner_ksi;


    //*****************CLIENTS BEGINNING****************************
    // funkcja aktualizująca listę klientów
    private void AktualnaListaKlientów(JList<String> lis) {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT klienci.pesel, nazwisko, imie, adres FROM klienci, kontakty WHERE klienci.pesel = kontakty.pesel ORDER BY nazwisko, imie";
            ResultSet res = stmt.executeQuery(sql);
            lmodel_kli.clear();
            while (res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + " " + res.getString(3) + ", " + res.getString(4);
                lmodel_kli.addElement(s);
            }
            stmt.close();
        } catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy klientów");
        }
    }

    // delegat obsługujący zdarzenie akcji od przycisku 'zapisz klienta'
    private ActionListener akc_zap_kli = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String pesel = pole_pesel.getText();
            if (!pesel.matches("[0-9]{3,11}")) {
                JOptionPane.showMessageDialog(Okno.this, "błąd w polu z peselm");
                pole_pesel.setText("");
                pole_pesel.requestFocus();
                return;
            }
            String imie = pole_im.getText();
            String nazwisko = pole_naz.getText();

            String ur = new SimpleDateFormat("YYYY-MM-dd").format(spinner_kli.getModel().getValue());

            if (imie.equals("") || nazwisko.equals("") || ur.equals("")) {
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z imieniem lub nazwiskiem lub datą urodzenia");
                return;
            }
            String mail = pole_mail.getText();
            String adr = pole_adr.getText();
            String tel = pole_tel.getText();
            if (mail.equals("") || adr.equals("")) {
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z emailem lub adresem");
                return;
            }
            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql1 = "INSERT INTO klienci (pesel, imie, nazwisko, ur) VALUES('" + pole_pesel.getText() + "', '" + pole_im.getText() + "', '" + pole_naz.getText() + "', '" + ur + "')";
                int res = stmt.executeUpdate(sql1);
                if (res == 1) {
                    komunikat.setText("OK - klient dodany do bazy");
                    String sql2 = "INSERT INTO kontakty (pesel, mail, adres, tel) VALUES('" + pole_pesel.getText() + "', '" + pole_mail.getText() + "', '" + pole_adr.getText() + "', '" + pole_tel.getText() + "')";
                    stmt.executeUpdate(sql2);
                    AktualnaListaKlientów(l_kli);
                }
                stmt.close();
            } catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie zapisano klienta");
            }
            //clear form after providing data
            pole_pesel.setText("");
            pole_im.setText("");
            pole_naz.setText("");
            pole_mail.setText("");
            pole_adr.setText("");
            pole_tel.setText("");

            //refreshing list of clients at the "Zamowienie" panel after adding a new client
            AktualnaListaKlientów(l_kli);
            clientList = new String[lmodel_kli.size()];
            clientList = getListOfClients();
            pole_clients.setModel(new DefaultComboBoxModel(clientList));
        }
    };

    // delegat obsługujący zdarzenie akcji od przycisku 'usuń klienta'
    private ActionListener akc_usun_kli = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (l_kli.isSelectionEmpty()){
                JOptionPane.showMessageDialog(Okno.this,"Please select the item from the client list!","Warning",JOptionPane.WARNING_MESSAGE);
                return;
            }
            String p = l_kli.getModel().getElementAt(l_kli.getSelectionModel().getMinSelectionIndex());
            p = p.substring(0, p.indexOf(':'));
            JOptionPane.showMessageDialog(Okno.this, "pesel: " + p);
            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql = "SELECT COUNT(*) FROM zamowienia WHERE pesel = '" + p + "'";
                ResultSet res = stmt.executeQuery(sql);
                res.next();
                int k = res.getInt(1);
                if (k == 0) {
                    String sql1 = "DELETE FROM klienci WHERE pesel = '" + p + "'";
                    stmt.executeUpdate(sql1);
                    String sql2 = "DELETE FROM kontakty WHERE pesel = '" + p + "'";
                    stmt.executeUpdate(sql2);
                    komunikat.setText("OK - klient usunięty bazy");
                    AktualnaListaKlientów(l_kli);
                } else komunikat.setText("nie usunięto klienta, ponieważ składał już zamówienia");
            } catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie ununięto klienta");
            }

            //refreshing list of clients at the "Zamowienie" panel after removing a client
            AktualnaListaKlientów(l_kli);
            clientList = new String[lmodel_kli.size()];
            clientList = getListOfClients();
            pole_clients.setModel(new DefaultComboBoxModel(clientList));
        }
    };
    //*****************CLIENTS END****************************

    //*****************BOOKS BEGINNING****************************
    // funkcja aktualizująca listę ksiazek
    private void AktualnaListaKsiazek(JList<String> lis1) {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM ksiazki";
            ResultSet res = stmt.executeQuery(sql);
            lmodel_ksi.clear();
            while (res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + ", " + res.getString(3) + ", " + res.getString(4) + ", " + res.getString(5) + ", " + res.getString(6) + ", " + res.getString(7);
                lmodel_ksi.addElement(s);
            }
            stmt.close();
        } catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy klientów");
        }
    }

    // delegat obsługujący zdarzenie akcji od przycisku 'zapisz ksiazke'
    private ActionListener akc_zap_ksi = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String isbn = pole_isbn.getText();
            String author = pole_author.getText();
            String title = pole_title.getText();
            if (isbn.equals("") || author.equals("") || title.equals("")) {
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z isbn lub autorem lub tytulem");
                return;
            }
            String typ = (String) pole_type.getSelectedItem();

            String publisher = pole_publisher.getText();

            String year = new SimpleDateFormat("YYYY").format(spinner_ksi.getModel().getValue());
            JOptionPane.showMessageDialog(Okno.this,"year: "+year);


            //price validation
            Double price = Double.valueOf(0);
            try {
                price = Double.parseDouble(pole_price.getText());
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(Okno.this, "Please put the right format of price here", "Error", JOptionPane.ERROR_MESSAGE);
            }

            if (price <= 0) {
                JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z cena lub nie poprawny format");
                return;
            }


            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql1 = "INSERT INTO ksiazki (isbn, autor, tytul, typ, wydawnictwo,rok,cena) VALUES('" + pole_isbn.getText() + "', '" + pole_author.getText() + "', '" + pole_title.getText() + "', '" + typ + "', '" + pole_publisher.getText() + "', '" + year + "', '" + pole_price.getText() + "')";
                int res = stmt.executeUpdate(sql1);
                if (res == 1) {
                    komunikat.setText("OK - ksiazka dodana do bazy");
                    AktualnaListaKsiazek(l_ksi);
                }
                stmt.close();
            } catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie zapisano ksiazki");
            }
            //clear form after providing data and successfully putting it into db
            pole_isbn.setText("");
            pole_author.setText("");
            pole_title.setText("");
            pole_publisher.setText("");
            pole_price.setText("");

            //refreshing list of books at the "Zamowienie" panel after adding a new book
            AktualnaListaKsiazek(l_ksi);
            bookList = new String[lmodel_ksi.size()];
            bookList = getListOfBooks();
            pole_books.setModel(new DefaultComboBoxModel(bookList));
        }
    };

    // delegat obsługujący zdarzenie akcji od przycisku 'usuń ksiazke'
    private ActionListener akc_usun_ksi = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (l_ksi.isSelectionEmpty()) {
                JOptionPane.showMessageDialog(Okno.this,"Please select the item from the book list!","Warning",JOptionPane.WARNING_MESSAGE);
                return;
            }
            String p = l_ksi.getModel().getElementAt(l_ksi.getSelectionModel().getMinSelectionIndex());
            p = p.substring(0, p.indexOf(':'));
            JOptionPane.showMessageDialog(Okno.this, "isbn: " + p);
            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql = "SELECT COUNT(*) FROM zestawienia WHERE isbn = '" + p + "'";
                ResultSet res = stmt.executeQuery(sql);
                res.next();
                int k = res.getInt(1);
                if (k == 0) {
                    String sql1 = "DELETE FROM ksiazki WHERE isbn = '" + p + "'";
                    stmt.executeUpdate(sql1);
                    komunikat.setText("OK - ksiazka usunięta z bazy");
                    AktualnaListaKsiazek(l_ksi);
                } else komunikat.setText("nie usunięto ksiazke, jest w zestawieniu");
            } catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie ununięto ksiazke");
            }

            //refreshing list of books at the "Zamowienie" panel after remowing a new book
            AktualnaListaKsiazek(l_ksi);
            bookList = new String[lmodel_ksi.size()];
            bookList = getListOfBooks();
            pole_books.setModel(new DefaultComboBoxModel(bookList));
        }
    };

    // delegat obsługujący zdarzenie akcji od przycisku 'zmien cene'
    private ActionListener akc_zmien_cene_ksi = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (l_ksi.isSelectionEmpty()){
                JOptionPane.showMessageDialog(Okno.this,"Please select the item from the book list!","Warning",JOptionPane.WARNING_MESSAGE);
                return;
            }
            String p = l_ksi.getModel().getElementAt(l_ksi.getSelectionModel().getMinSelectionIndex());
            p = p.substring(0, p.indexOf(':'));
            JOptionPane.showMessageDialog(Okno.this, "isbn: " + p);
            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql = "SELECT * FROM ksiazki WHERE isbn = '" + p + "'";
                ResultSet res = stmt.executeQuery(sql);
                while (res.next()) {
                    //set invisible unnecessary fields and buttons
                    przyc_usun_ksi.setVisible(false);
                    przyc_zapisz_ksi.setVisible(false);
                    przyc_zmien_cena_ksi.setVisible(false);
                    pole_isbn.setVisible(false);
                    pole_isbn.setText(res.getString(1));
                    pole_author.setVisible(false);
                    pole_author.setText(res.getString(2));
                    pole_title.setVisible(false);
                    pole_title.setText(res.getString(3));
                    pole_type.setVisible(false);
                    //Choose the type of selected item
                    int index = 0;
                    for (int i = 0; i < items.length; i++) {
                        if (items[i].equals(res.getString(4))) {
                            index = i;
                        }
                    }
                    pole_type.getItemAt(index);

                    pole_publisher.setVisible(false);
                    pole_publisher.setText(res.getString(5));
                    spinner_ksi.setVisible(false);
                    //show button for saving changes in the price
                    przyc_zapisz_zmiane_ksi.setVisible(true);
                    //price validation
                    double price = Double.valueOf(0);
                    try {
                        price = Double.parseDouble(res.getString(7));
                        pole_price.setText(Double.toString(price));
                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(Okno.this, "Please right number here", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    if (price == 0) {
                        JOptionPane.showMessageDialog(Okno.this, "nie wypełnione pole z rokiem lub cena lub wydawnictwem", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                stmt.close();
            } catch (SQLException ex) {
                komunikat.setText("błąd SQL - niepowodzenie w odczytaniu danych z bazy danych");
            }
        }
    };

    //Saving the book price changes into db
    private ActionListener akc_zapisz_zmiane_ksi = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql1 = "UPDATE ksiazki SET cena = '" + pole_price.getText() + "'WHERE isbn = '" + pole_isbn.getText() + "'";
                stmt.executeUpdate(sql1);
                komunikat.setText("OK - zmiana zostala wprowadzona");
                AktualnaListaKsiazek(l_ksi);
                stmt.close();
            } catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie zapisano zmian w cenie ksiazki");
            }

            //return to default bookmark layout for 'ksazki'
            przyc_zmien_cena_ksi.setVisible(true);
            przyc_usun_ksi.setVisible(true);
            przyc_zapisz_ksi.setVisible(true);
            przyc_zapisz_zmiane_ksi.setVisible(false);
            pole_isbn.setVisible(true);
            pole_author.setVisible(true);
            pole_title.setVisible(true);
            pole_type.setVisible(true);
            pole_publisher.setVisible(true);
            spinner_ksi.setVisible(true);
            //clear input fields
            pole_isbn.setText("");
            pole_author.setText("");
            pole_title.setText("");
            pole_type.getName();
            pole_publisher.setText("");
            pole_price.setText("");
        }
    };
    //*****************BOOKS END****************************

    //*****************ORDERS BEGINNING**********************
    // funkcja aktualizująca listę klientów
    private String[] getListOfClients() {

        try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM klienci";
            ResultSet res = stmt.executeQuery(sql);
            int i = 0;
            while (res.next()) {
                String s = res.getString(1) + ": " + res.getString(3);
                clientList[i] = s;
                i++;
            }
            stmt.close();
        } catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy klientów");
        }
        return clientList;
    }

    // funkcja aktualizująca listę ksiazek
    private String[] getListOfBooks() {

        try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM ksiazki";
            ResultSet res = stmt.executeQuery(sql);
            int i = 0;
            while (res.next()) {
                String s = res.getString(1) + ": " + res.getString(3) + " " + res.getString(7);
                bookList[i] = s;
                i++;
            }
            stmt.close();
        } catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy ksiazek");
        }
        return bookList;
    }

    // funkcja aktualizująca listę zamowien
    private void AktualnaListaZamowien(JList<String> lis1) {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM zamowienia";
            ResultSet res = stmt.executeQuery(sql);
            lmodel_zam.clear();
            while (res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + ", " + res.getString(4) + ", " + res.getString(5);
                lmodel_zam.addElement(s);
            }
            stmt.close();
        } catch (SQLException ex) {
            komunikat.setText("nie udało się zaktualizować listy zamowien");
        }
    }

    // delegat obsługujący zdarzenie akcji od przycisku 'zmien status'
    private ActionListener akc_zmien_status_zam = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (l_zam.isSelectionEmpty()) {
                JOptionPane.showMessageDialog(Okno.this,"Please select the item from the order list!","Warning",JOptionPane.WARNING_MESSAGE);
                return;
            }

            pole_clients.setVisible(false);
            spinner_zam.setVisible(false);
            pole_books.setVisible(false);
            przyc_zapisz_zam.setVisible(false);
            przyc_zmien_status_zam.setVisible(false);
            przyc_zatwierdz_zam.setVisible(true);
        }
    };

    //delegat obsługujący zdarzenie akcji od przycisku 'zatwierdz zmiane statusu zamowienia'
    private ActionListener akc_zatwierdz_zmiane_statusu_zam = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                String id = l_zam.getModel().getElementAt(l_zam.getSelectionModel().getMinSelectionIndex());
                id = id.substring(0,id.indexOf(':'));
                JOptionPane.showMessageDialog(Okno.this, "id: " + id);
                String s = (String) pole_status.getSelectedItem();

                JOptionPane.showMessageDialog(Okno.this, "status: " + s);

                Statement stmt = conn.createStatement();
                String sql1 = "UPDATE zamowienia SET status = '" + s + "'WHERE id = '" + id + "'";
                stmt.executeUpdate(sql1);
                komunikat.setText("OK - zmiana statusu zamowienia id = "+id+" zostala wprowadzona");

                pole_clients.setVisible(true);
                spinner_zam.setVisible(true);
                pole_books.setVisible(true);
                przyc_zapisz_zam.setVisible(true);
                przyc_zmien_status_zam.setVisible(true);
                przyc_zatwierdz_zam.setVisible(false);

                AktualnaListaZamowien(l_zam);
                stmt.close();
            } catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie zapisano zmian statusa zamowienia");
            }
        }
    };

    // delegat obsługujący zdarzenie akcji od przycisku 'zapisz zamowienie'
    private ActionListener akc_zapisz_zam = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {

                String p = (String) pole_clients.getSelectedItem();
                String pl = p.substring(0, p.indexOf(':'));
                String shipment = new SimpleDateFormat("YYYY-MM-dd").format(spinner_zam.getModel().getValue());
                JOptionPane.showMessageDialog(Okno.this,"date: "+shipment);
                String status = (String) pole_status.getSelectedItem();
                String b = (String) pole_books.getSelectedItem();
                String book = b.substring(0, b.indexOf(':'));

                Statement stmt = conn.createStatement();
                String sql1 = "INSERT INTO zamowienia (id, pesel, kiedy, status, isbn) VALUES(null,'"+pl+"','"+shipment+"','"+status+"','"+book+"')";
                int res = stmt.executeUpdate(sql1);
                if (res == 1) {
                    komunikat.setText("OK - zamowienie jest dodane do bazy");
                    //extract price from table 'ksiazki'
                    String sql3 = "SELECT * FROM ksiazki WHERE isbn = '"+book+"'";
                    ResultSet res3 = stmt.executeQuery(sql3);
                    res3.next();
                    double cena = Double.parseDouble(res3.getString(7));
                    //put extracted price from sql3 into sql2
                    String sql2 = "INSERT INTO zestawienia (id, isbn, cena) VALUES(null,'" + book + "','"+cena+"')";
                    stmt.executeUpdate(sql2);
                    JOptionPane.showMessageDialog(Okno.this,"The book with isbn "+book+" is added to table 'zestawienia'");
                    AktualnaListaZamowien(l_zam);
                }
                stmt.close();
            } catch (SQLException ex) {
                komunikat.setText("błąd SQL - nie zapisano zamowienia");
            }
        }
    };

    //*****************ORDERS END****************************


    //Constructor
    public Okno() throws SQLException {
        super("Księgarnia wysyłkowa");
        setSize(660, 460);
        this.setLocationRelativeTo(null);
        setResizable(false);
        //******************** CLIENTS *************************
        // panel do zarządzania klientami
        p_kli.setLayout(null);
        // pole z peselem
        JLabel lab1 = new JLabel("pesel:");
        p_kli.add(lab1);
        lab1.setSize(100, 20);
        lab1.setLocation(40, 40);
        lab1.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_pesel);
        pole_pesel.setSize(200, 20);
        pole_pesel.setLocation(160, 40);
        // pole z imieniem
        JLabel lab2 = new JLabel("imię:");
        p_kli.add(lab2);
        lab2.setSize(100, 20);
        lab2.setLocation(40, 80);
        lab2.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_im);
        pole_im.setSize(200, 20);
        pole_im.setLocation(160, 80);
        // pole z nazwiskiem
        JLabel lab3 = new JLabel("nazwisko:");
        p_kli.add(lab3);
        lab3.setSize(100, 20);
        lab3.setLocation(40, 120);
        lab3.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_naz);
        pole_naz.setSize(200, 20);
        pole_naz.setLocation(160, 120);
        // pole z datą urodzenia
        JLabel lab4 = new JLabel("data urodzenia:");
        p_kli.add(lab4);
        lab4.setSize(100, 20);
        lab4.setLocation(40, 160);
        lab4.setHorizontalTextPosition(JLabel.RIGHT);
        spinner_kli= new JSpinner(new SpinnerDateModel(todaysDate,null,null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor_kli = new JSpinner.DateEditor(spinner_kli,"dd/MM/yyyy");
        spinner_kli.setEditor(dateEditor_kli);
        p_kli.add(spinner_kli);
        spinner_kli.setSize(200,20);
        spinner_kli.setLocation(160,160);
        // pole z mailem
        JLabel lab5 = new JLabel("mail:");
        p_kli.add(lab5);
        lab5.setSize(100, 20);
        lab5.setLocation(40, 200);
        lab5.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_mail);
        pole_mail.setSize(200, 20);
        pole_mail.setLocation(160, 200);
        // pole z adresem
        JLabel lab6 = new JLabel("adres:");
        p_kli.add(lab6);
        lab6.setSize(100, 20);
        lab6.setLocation(40, 240);
        lab6.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_adr);
        pole_adr.setSize(200, 20);
        pole_adr.setLocation(160, 240);
        // pole z telefonem
        JLabel lab7 = new JLabel("telefon:");
        p_kli.add(lab7);
        lab7.setSize(100, 20);
        lab7.setLocation(40, 280);
        lab7.setHorizontalTextPosition(JLabel.RIGHT);
        p_kli.add(pole_tel);
        pole_tel.setSize(200, 20);
        pole_tel.setLocation(160, 280);
        // przycisk do zapisu klienta
        p_kli.add(przyc_zapisz_kli);
        przyc_zapisz_kli.setSize(200, 20);
        przyc_zapisz_kli.setLocation(160, 320);
        przyc_zapisz_kli.addActionListener(akc_zap_kli);
        // przycisk do usunięcia klienta
        p_kli.add(przyc_usun_kli);
        przyc_usun_kli.setSize(200, 20);
        przyc_usun_kli.setLocation(400, 320);
        przyc_usun_kli.addActionListener(akc_usun_kli);
        // lista z klientami
        p_kli.add(sp_kli);
        sp_kli.setSize(200, 260);
        sp_kli.setLocation(400, 40);
        l_kli.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AktualnaListaKlientów(l_kli);
        //******************** BOOKS *************************
        //panel z ksiazkami
        p_ksi.setLayout(null);
        // pole z isbn
        JLabel lab11 = new JLabel("isbn:");
        p_ksi.add(lab11);
        lab11.setSize(100, 20);
        lab11.setLocation(40, 40);
        lab11.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_isbn);
        pole_isbn.setSize(200, 20);
        pole_isbn.setLocation(160, 40);
        // pole z imieniem autora
        JLabel lab22 = new JLabel("author:");
        p_ksi.add(lab22);
        lab22.setSize(100, 20);
        lab22.setLocation(40, 80);
        lab22.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_author);
        pole_author.setSize(200, 20);
        pole_author.setLocation(160, 80);
        // pole z tytulem
        JLabel lab33 = new JLabel("title:");
        p_ksi.add(lab33);
        lab33.setSize(100, 20);
        lab33.setLocation(40, 120);
        lab33.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_title);
        pole_title.setSize(200, 20);
        pole_title.setLocation(160, 120);
        // pole z typem
        JLabel lab44 = new JLabel("type:");
        p_ksi.add(lab44);
        lab44.setSize(100, 20);
        lab44.setLocation(40, 160);
        lab44.setHorizontalTextPosition(JLabel.RIGHT);
        pole_type = new JComboBox(items);
        pole_type.setSize(200, 20);
        pole_type.setLocation(160, 160);
        p_ksi.add(pole_type);
        // pole z wydawnictwem
        JLabel lab55 = new JLabel("publisher:");
        p_ksi.add(lab55);
        lab55.setSize(100, 20);
        lab55.setLocation(40, 200);
        lab55.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_publisher);
        pole_publisher.setSize(200, 20);
        pole_publisher.setLocation(160, 200);
        // pole z rokiem
        JLabel lab66 = new JLabel("year:");
        p_ksi.add(lab66);
        lab66.setSize(100, 20);
        lab66.setLocation(40, 240);
        lab66.setHorizontalTextPosition(JLabel.RIGHT);
        spinner_ksi= new JSpinner(new SpinnerDateModel(todaysDate,null,null, Calendar.YEAR));
        JSpinner.DateEditor dateEditor_ksi = new JSpinner.DateEditor(spinner_ksi,"yyyy");
        spinner_ksi.setEditor(dateEditor_ksi);
        p_ksi.add(spinner_ksi);
        spinner_ksi.setSize(200,20);
        spinner_ksi.setLocation(160,240);
        // pole z cena
        JLabel lab77 = new JLabel("price:");
        p_ksi.add(lab77);
        lab77.setSize(100, 20);
        lab77.setLocation(40, 280);
        lab77.setHorizontalTextPosition(JLabel.RIGHT);
        p_ksi.add(pole_price);
        pole_price.setSize(200, 20);
        pole_price.setLocation(160, 280);
        // przycisk do zapisu ksiazki
        p_ksi.add(przyc_zapisz_ksi);
        przyc_zapisz_ksi.setSize(200, 20);
        przyc_zapisz_ksi.setLocation(160, 320);
        przyc_zapisz_ksi.addActionListener(akc_zap_ksi);
        // przycisk do usunięcia ksiazki
        p_ksi.add(przyc_usun_ksi);
        przyc_usun_ksi.setSize(200, 20);
        przyc_usun_ksi.setLocation(400, 320);
        przyc_usun_ksi.addActionListener(akc_usun_ksi);
        // przycisk do zmien cene ksiazki
        p_ksi.add(przyc_zmien_cena_ksi);
        przyc_zmien_cena_ksi.setSize(200, 20);
        przyc_zmien_cena_ksi.setLocation(400, 360);
        przyc_zmien_cena_ksi.addActionListener(akc_zmien_cene_ksi);
        // przycisk do zmien cene ksiazki
        p_ksi.add(przyc_zapisz_zmiane_ksi);
        przyc_zapisz_zmiane_ksi.setSize(200, 20);
        przyc_zapisz_zmiane_ksi.setLocation(160, 320);
        przyc_zapisz_zmiane_ksi.addActionListener(akc_zapisz_zmiane_ksi);
        przyc_zapisz_zmiane_ksi.setVisible(false);
        // lista z ksiazkami
        p_ksi.add(sp_ksi);
        sp_ksi.setSize(200, 260);
        sp_ksi.setLocation(400, 40);
        l_ksi.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AktualnaListaKsiazek(l_ksi);
        //******************** ORDERS *************************
        //panel z zamowieniami
        p_zam.setLayout(null);
        // pole z client
        JLabel lab111 = new JLabel("Client:");
        p_zam.add(lab111);
        clientList = new String[lmodel_kli.size()];
        pole_clients = new JComboBox(getListOfClients());
        lab111.setSize(100, 20);
        lab111.setLocation(40, 40);
        lab111.setHorizontalTextPosition(JLabel.RIGHT);
        p_zam.add(pole_clients);
        pole_clients.setSize(200, 20);
        pole_clients.setLocation(160, 40);
        // pole z date
        JLabel lab222 = new JLabel("Shipment Date:");
        p_zam.add(lab222);
        lab222.setSize(100, 20);
        lab222.setLocation(40, 80);
        lab222.setHorizontalTextPosition(JLabel.RIGHT);
        spinner_zam= new JSpinner(new SpinnerDateModel(todaysDate,null,null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinner_zam,"dd/MM/yyyy");
        spinner_zam.setEditor(dateEditor);
        p_zam.add(spinner_zam);
        spinner_zam.setSize(200,20);
        spinner_zam.setLocation(160,80);
        // pole z tytulem
        JLabel lab333 = new JLabel("Status:");
        p_zam.add(lab333);
        lab333.setSize(100, 20);
        lab333.setLocation(40, 120);
        lab333.setHorizontalTextPosition(JLabel.RIGHT);
        pole_status = new JComboBox(status);
        p_zam.add(pole_status);
        pole_status.setSize(200, 20);
        pole_status.setLocation(160, 120);
        // pole z ksiazkami do formowania zestawow
        JLabel lab444 = new JLabel("Books:");
        p_zam.add(lab444);
        lab444.setSize(100, 20);
        lab444.setLocation(40, 160);
        lab444.setHorizontalTextPosition(JLabel.RIGHT);
        bookList = new String[lmodel_ksi.size()];
        pole_books = new JComboBox(getListOfBooks());
        pole_books.setSize(200, 20);
        pole_books.setLocation(160, 160);
        p_zam.add(pole_books);
        // przycisk do zmiany statusu
        p_zam.add(przyc_zmien_status_zam);
        przyc_zmien_status_zam.setSize(200, 20);
        przyc_zmien_status_zam.setLocation(400, 320);
        przyc_zmien_status_zam.addActionListener(akc_zmien_status_zam);
        // przycisk do zatwierdzenia zmiane statusu
        p_zam.add(przyc_zatwierdz_zam);
        przyc_zatwierdz_zam.setSize(200, 20);
        przyc_zatwierdz_zam.setLocation(400, 360);
        przyc_zatwierdz_zam.addActionListener(akc_zatwierdz_zmiane_statusu_zam);
        przyc_zatwierdz_zam.setVisible(false);
        // przycisk do zapisu zamowienia
        p_zam.add(przyc_zapisz_zam);
        przyc_zapisz_zam.setSize(200, 20);
        przyc_zapisz_zam.setLocation(160, 320);
        przyc_zapisz_zam.addActionListener(akc_zapisz_zam);
        // lista zamowien
        p_zam.add(sp_zam);
        sp_zam.setSize(200, 260);
        sp_zam.setLocation(400, 40);
        l_zam.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AktualnaListaZamowien(l_zam);
        //************************************************************
        // panel z zakładkami
        tp.addTab("klienci", p_kli);
        tp.addTab("książki", p_ksi);
        tp.addTab("zamówienia", p_zam);
        getContentPane().add(tp, BorderLayout.CENTER);
        // pole na komentarze
        komunikat.setEditable(false);
        getContentPane().add(komunikat, BorderLayout.SOUTH);
        // pokazanie okna
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        new Okno();
    }
}
