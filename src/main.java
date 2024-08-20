import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sehaf
 */
public class main extends JFrame {

    /**
     * -1 empty 1 bomb 2 flag 3 flag on bomb
     */
    int size;
    int distance;
    int dif;
    int flag;
    int[][] mat;
    int[][] nums;
    boolean[][] show;
    boolean gameOver;
    boolean shiftDown;

    JButton newG = new JButton("New Game");
    JLabel flags = new JLabel("Flags: 15");
    JComboBox<String> dificulty = new JComboBox<>();
    JPanel controles = new JPanel();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new main();
    }

    public main() {
        setTitle("Bombs go burrrr");
        setVisible(true);
        setResizable(true);
        shiftDown = false;
        validate();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((int) (.5 * (screensize.width - getWidth())), (int) (.5 * (screensize.height - getHeight())), getWidth(), getHeight());
        setUpMain();
        addBs();
        addNums();
    }

    public void setUpMain() {
        DrawingPanel draw = new DrawingPanel();
        draw.setBackground(Color.GRAY);
        draw.setPreferredSize(new Dimension(410, 410));
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        makeControles();
        controles.setBackground(Color.GRAY);
        controles.add(dificulty);
        controles.add(newG);
        controles.add(flags);
        controles.setPreferredSize(new Dimension(350, 80));
        getContentPane().add(controles);
        getContentPane().add(draw);
        reset("*Normal*");
        pack();
        addClickListner(draw);
    }

    public void reset(String ch) {
        if (ch.equals("Easy")) {
            size = 5;
            distance = 60;
            dif = 4;
            flags.setText("Flags: 4");
            controles.setPreferredSize(new Dimension(350, 80));
            controles.setLayout(new FlowLayout());
            getContentPane().setPreferredSize(new Dimension(310, 380));
            pack();
        } else if (ch.equals("*Normal*")) {
            size = 10;
            distance = 40;
            dif = (int) (size * 1.5);
            flags.setText("Flags: 15");
            controles.setPreferredSize(new Dimension(350, 80));
            controles.setLayout(new FlowLayout());
            getContentPane().setPreferredSize(new Dimension(405, 495));
            pack();
        } else if (ch.equals("***Hard***")) {
            size = 20;
            distance = 25;
            dif = 100;
            controles.setPreferredSize(new Dimension(350, 0));
            controles.setLayout(new BoxLayout(controles, BoxLayout.Y_AXIS));
            getContentPane().setPreferredSize(new Dimension(508, 605));
            pack();
            flags.setText("Flags: 100");
        } else {
            size = 40;
            distance = 13;
            dif = 897;
            flags.setText("Flags: 897");
            controles.setPreferredSize(new Dimension(350, 0));
            controles.setLayout(new BoxLayout(controles, BoxLayout.Y_AXIS));
            getContentPane().setPreferredSize(new Dimension(526, 650));
            pack();
        }
        flag = dif;
        mat = new int[size][size];
        nums = new int[size][size];
        show = new boolean[size][size];
        gameOver = false;
        for (boolean[] show1 : show) {
            for (int j = 0; j < show1.length; j++) {
                show1[j] = false;
            }
        }
        addBs();
        addNums();
        repaint();
    }

    public void makeControles() {
        newG.addActionListener((ae) -> {
            reset(dificulty.getSelectedItem().toString());
        });

        Border b = BorderFactory.createEtchedBorder(Color.BLUE, Color.CYAN);
        String[] s = {"Easy", "*Normal*", "***Hard***", "AAAHHHHH!!!"};
        dificulty = new JComboBox<>(s);
        dificulty.setSelectedIndex(1);
        dificulty.setVisible(true);
        dificulty.setBackground(Color.WHITE);
        dificulty.setForeground(Color.BLACK);
        dificulty.setPreferredSize(new Dimension(150, 30));
        dificulty.setFont(new Font(Font.SERIF, Font.ITALIC, 20));
        dificulty.setBorder(b);

        dificulty.addActionListener((ae) -> {
            JComboBox cb = (JComboBox) ae.getSource();
            String cur = (String) cb.getSelectedItem();
            reset(cur);
        });

        newG.setBackground(Color.WHITE);
        newG.setForeground(Color.BLACK);
        newG.setPreferredSize(new Dimension(150, 30));
        newG.setFont(new Font(Font.SERIF, Font.ITALIC, 20));
        newG.setBorder(b);
        flags.setPreferredSize(new Dimension(120, 30));
        flags.setForeground(Color.BLACK);
        flags.setBorder(b);
        flags.setFont(new Font(Font.SERIF, Font.ITALIC, 20));
        flags.setBackground(Color.WHITE);
        flags.setOpaque(true);
    }

    public void addNums() {
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                nums[i][j] = getDispNum(j, i);
            }
        }
        repaint();
    }

    public void addBs() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                mat[i][j] = -1;
            }
        }
        TreeMap<Integer, Integer> m = new TreeMap<>();
        for (int i = 0; i < dif; i++) {
            int r1 = (int) (Math.random() * size);
            int r2 = (int) (Math.random() * size);
            if (m.containsKey(r1) && m.get(r1).equals(r2)) {
                while (m.containsKey(r1) && m.get(r1).equals(r2)) {
                    r1 = (int) (Math.random() * size);
                    r2 = (int) (Math.random() * size);
                }
            }
            mat[r1][r2] = 1;
            m.put(r1, r2);
        }
    }

    public int figureOutNums(int x, int y) {
        if (x >= size || y >= size || x < 0 || y < 0) {
            return 0;
        } else {
            if (mat[y][x] == 1 || mat[y][x] == 3) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public int getDispNum(int x, int y) {
        return (figureOutNums(x - 1, y) + figureOutNums(x, y - 1) + figureOutNums(x - 1, y - 1) + figureOutNums(x + 1, y) + figureOutNums(x, y + 1) + figureOutNums(x + 1, y + 1) + figureOutNums(x + 1, y - 1) + figureOutNums(x - 1, y + 1));
    }

    public void showSuroundingsAfterClick(int x, int y) {
        if (x < size && y < size && x >= 0 && y >= 0) {
            if (mat[y][x] == 1) {
                gameOver = true;
                try {
                    lossSong();
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (show[y][x] == false) {
                if (nums[y][x] == 0) {
                    if (mat[y][x] == -1) {
                        show[y][x] = true;
                    }
                    showSuroundingsAfterClick(x - 1, y);
                    showSuroundingsAfterClick(x, y - 1);
                    //showSuroundingsAfterClick(x - 1, y - 1);
                    showSuroundingsAfterClick(x + 1, y);
                    showSuroundingsAfterClick(x, y + 1);
                    //showSuroundingsAfterClick(x + 1, y + 1);
                    //showSuroundingsAfterClick(x + 1, y - 1);
                    //showSuroundingsAfterClick(x - 1, y + 1);
                } else if (nums[y][x] != 0) {
                    show[y][x] = true;
                }
            }
        }
    }

    private class DrawingPanel extends JPanel {

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            for (int i = 0; i <= size; i++) {
                g2d.drawLine(i * distance + 3, 3, i * distance + 3, size * distance + 3);
                g2d.drawLine(3, i * distance + 3, size * distance + 3, i * distance + 3);
            }
            int correct = 0;
            for (int i = 0; i < mat.length; i++) {
                for (int j = 0; j < mat[i].length; j++) {
                    //flag (green) || flag on bomb
                    if (mat[i][j] == 2 || mat[i][j] == 3) {
                        g2d.setColor(Color.GREEN);
                        g2d.fillRect(j * distance + 4, i * distance + 4, distance - 1, distance - 1);
                        if (mat[i][j] == 3) {
                            correct++;
                        }
                    } else if (mat[i][j] == -1 || mat[i][j] == 1) {
                        g2d.setColor(Color.DARK_GRAY);
                        g2d.fillRect(j * distance + 4, i * distance + 4, distance - 1, distance - 1);
                    }
                    if (show[i][j]) {
                        g2d.setColor(Color.BLACK);
                        g2d.fillRect(j * distance + 4, i * distance + 4, distance - 1, distance - 1);
                        if (nums[i][j] != 0) {
                            g2d.setColor(Color.WHITE);
                            g2d.drawString(nums[i][j] + "", j * distance + (distance / 2), (i + 1) * distance - (distance / 2) + 8);
                        }
                    }
                    //bomb (red)
                    if (mat[i][j] == 1 && gameOver || mat[i][j] == 3 && gameOver) {
                        g2d.setColor(Color.RED);
                        g2d.fillRect(j * distance + 4, i * distance + 4, distance - 1, distance - 1);
                    }
                }
            }
            if (correct == flag) {
                gameOver = true;
                try {
                    winnerSong();
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void flagSong() throws UnsupportedAudioFileException, LineUnavailableException {
        try {
            AudioInputStream input = AudioSystem.getAudioInputStream(new File("Flag.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(input);
            clip.start();
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }        
    
    public void winnerSong() throws UnsupportedAudioFileException, LineUnavailableException {
        try {
            AudioInputStream input = AudioSystem.getAudioInputStream(new File("Winner.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(input);
            clip.start();
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    public void placeSong() throws UnsupportedAudioFileException, LineUnavailableException {
        try {
            AudioInputStream input = AudioSystem.getAudioInputStream(new File("Place.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(input);
            clip.start();
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void lossSong() throws UnsupportedAudioFileException, LineUnavailableException {
        try {
            AudioInputStream input = AudioSystem.getAudioInputStream(new File("Loss.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(input);
            clip.start();
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addClickListner(JPanel d) {
        d.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                int i = (int) (Math.floor(e.getY() / distance));
                int j = (int) (Math.floor(e.getX() / distance));
                if(shiftDown){
                    mat[i][j] = 1;
                }
                if (!gameOver) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        try {
                            flagSong();
                        } catch (UnsupportedAudioFileException ex) {
                            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (LineUnavailableException ex) {
                            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (mat[i][j] == 2) {
                            mat[i][j] = -1;
                            dif++;
                            flags.setText("Flags: " + dif);
                        } else if (mat[i][j] == 3) {
                            mat[i][j] = 1;
                            dif++;
                            flags.setText("Flags: " + dif);
                        } else if (mat[i][j] == 1 && dif - 1 >= 0) {
                            mat[i][j] = 3;
                            dif--;
                            flags.setText("Flags: " + dif);
                        } else {
                            if (dif - 1 >= 0) {
                                dif--;
                                flags.setText("Flags: " + dif);
                                mat[i][j] = 2;
                            }
                        }
                    } else if (e.getButton() == MouseEvent.BUTTON1) {
                        if (mat[i][j] == -1 || mat[i][j] == 1) {
                            try {
                                placeSong();
                            } catch (UnsupportedAudioFileException ex) {
                                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (LineUnavailableException ex) {
                                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            showSuroundingsAfterClick(j, i);
                        }
                    }
                    repaint();
                }
            }
        });
    }
}
