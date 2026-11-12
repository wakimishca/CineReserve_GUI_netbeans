import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class CineReserve extends JFrame {

    private JComboBox<String> movieBox;
    private JComboBox<String> timeBox;

    private JButton[][] seats = new JButton[6][8];

    private boolean[][] seatSelected = new boolean[6][8];
    private boolean[][] seatReserved = new boolean[6][8];

    private JLabel timerLabel;
    private JLabel totalLabel;

    private Timer timer;

    private int timeLeft = 300;
    private int ticketPrice = 250;

    // Reservation storage
    private HashMap<String, boolean[][]> reservations = new HashMap<>();

    public CineReserve(){

        setTitle("CineReserve - Cinema Ticket Booking");
        setSize(1000,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createHeader();
        createSeatArea();
        createBottomPanel();

        setVisible(true);
    }

    // HEADER
    private void createHeader(){

        JPanel top = new JPanel();
        top.setBackground(new Color(45,45,45));

        JLabel title = new JLabel("CineReserve");
        title.setFont(new Font("Arial",Font.BOLD,28));
        title.setForeground(Color.WHITE);

        String[] movies = {"Avengers","Batman","Spider-Man","Avatar"};
        movieBox = new JComboBox<>(movies);

        String[] times = {"10:00 AM","1:00 PM","4:00 PM","7:00 PM"};
        timeBox = new JComboBox<>(times);

        movieBox.addActionListener(e -> loadReservations());
        timeBox.addActionListener(e -> loadReservations());

        top.add(title);
        top.add(Box.createHorizontalStrut(40));

        top.add(new JLabel("Movie:"));
        top.add(movieBox);

        top.add(new JLabel("Showtime:"));
        top.add(timeBox);

        add(top,BorderLayout.NORTH);
    }

    // SEAT GRID
    private void createSeatArea(){

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(30,30,30));

        JLabel screen = new JLabel("SCREEN",SwingConstants.CENTER);
        screen.setForeground(Color.WHITE);
        screen.setFont(new Font("Arial",Font.BOLD,20));

        center.add(screen,BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(6,8,10,10));
        grid.setBorder(BorderFactory.createEmptyBorder(20,120,20,120));

        char rowLetter = 'A';

        for(int i=0;i<6;i++){

            for(int j=0;j<8;j++){

                String seatName = rowLetter+""+(j+1);

                JButton seat = new JButton(seatName);
                seat.setBackground(new Color(0,170,0));
                seat.setForeground(Color.WHITE);

                int r=i;
                int c=j;

                seat.addActionListener(e -> toggleSeat(r,c));

                seats[i][j] = seat;

                grid.add(seat);
            }

            rowLetter++;
        }

        center.add(grid,BorderLayout.CENTER);

        add(center,BorderLayout.CENTER);
    }

    // BOTTOM PANEL
    private void createBottomPanel(){

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(45,45,45));

        JButton confirm = new JButton("Confirm Booking");

        timerLabel = new JLabel("Timer: 5:00");
        timerLabel.setForeground(Color.WHITE);

        totalLabel = new JLabel("Total: ₱0");
        totalLabel.setForeground(Color.WHITE);

        confirm.addActionListener(e -> confirmBooking());

        bottom.add(confirm);
        bottom.add(timerLabel);
        bottom.add(totalLabel);

        add(bottom,BorderLayout.SOUTH);
    }

    // SEAT CLICK
    private void toggleSeat(int r,int c){

        if(seatReserved[r][c])
            return;

        if(seatSelected[r][c]){

            seatSelected[r][c] = false;
            seats[r][c].setBackground(new Color(0,170,0));

        }else{

            seatSelected[r][c] = true;
            seats[r][c].setBackground(Color.ORANGE);

            startTimer();
        }

        updateTotal();
    }

    // CONFIRM BOOKING
    private void confirmBooking(){

        String key = getCurrentKey();

        boolean[][] saved = reservations.getOrDefault(key,new boolean[6][8]);

        int count=0;

        String seatList="";

        for(int i=0;i<6;i++){
            for(int j=0;j<8;j++){

                if(seatSelected[i][j]){

                    saved[i][j] = true;
                    seatReserved[i][j] = true;

                    seats[i][j].setBackground(Color.RED);

                    seatList += seats[i][j].getText()+" ";

                    seatSelected[i][j] = false;

                    count++;
                }

            }
        }

        reservations.put(key,saved);

        if(count==0){

            JOptionPane.showMessageDialog(this,"Select seats first");

        }else{

            JOptionPane.showMessageDialog(this,
                    "Booking Confirmed\nSeats: "+seatList+
                            "\nTotal: ₱"+(count*ticketPrice));
        }

        updateTotal();
    }

    // LOAD SEATS WHEN MOVIE/TIME CHANGES
    private void loadReservations(){

        String key = getCurrentKey();

        boolean[][] saved = reservations.getOrDefault(key,new boolean[6][8]);

        for(int i=0;i<6;i++){
            for(int j=0;j<8;j++){

                seatSelected[i][j] = false;

                seatReserved[i][j] = saved[i][j];

                if(saved[i][j])
                    seats[i][j].setBackground(Color.RED);
                else
                    seats[i][j].setBackground(new Color(0,170,0));
            }
        }

        updateTotal();
    }

    // CURRENT MOVIE + TIME KEY
    private String getCurrentKey(){

        return movieBox.getSelectedItem()+"-"+timeBox.getSelectedItem();
    }

    // TOTAL CALCULATION
    private void updateTotal(){

        int count=0;

        for(int i=0;i<6;i++){
            for(int j=0;j<8;j++){

                if(seatSelected[i][j])
                    count++;
            }
        }

        totalLabel.setText("Total: ₱"+(count*ticketPrice));
    }

    // TIMER
    private void startTimer(){

        if(timer!=null && timer.isRunning())
            return;

        timer = new Timer(1000,e -> {

            timeLeft--;

            int min=timeLeft/60;
            int sec=timeLeft%60;

            timerLabel.setText("Timer: "+min+":"+String.format("%02d",sec));

            if(timeLeft<=0){

                releaseSeats();
                timer.stop();

                JOptionPane.showMessageDialog(this,"Reservation expired");
            }

        });

        timer.start();
    }

    // RELEASE SEATS
    private void releaseSeats(){

        for(int i=0;i<6;i++){
            for(int j=0;j<8;j++){

                if(seatSelected[i][j]){

                    seatSelected[i][j] = false;

                    seats[i][j].setBackground(new Color(0,170,0));
                }
            }
        }

        timeLeft=300;

        updateTotal();
    }

    public static void main(String[] args){

        SwingUtilities.invokeLater(() -> new CineReserve());

    }
}