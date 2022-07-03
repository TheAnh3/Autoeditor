/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package video;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IOpenCoderEvent;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author thean
 */
public class Autoeditor {

    private static final int width = 720;
    private static final int height = 480;
    private static final String titelWindow = "Autoeditor";
    private static final String path = "C:\\Autoeditor\\Background.jpg";
    private static final String path2 = "C:\\Autoeditor\\Scissors.gif";
    private static final String path3 = "C:\\Autoeditor\\Film2.gif";
    private static final String path4 = "C:\\Autoeditor\\BGM2.wav";
    private static boolean ButtonOn = false;
    private static boolean AlreadyPlaying = false;
    private static Clip clip = null;
    private static long clipTime = 0;
    private static String Videofile;
    private static String ToAudio = "C:\\Autoeditor\\Audio.mp3";
    private static boolean pickedVideo = false;
    private static boolean ConvertedToAudio = false;
    private static ArrayList<Double> GetSilenceTimeStamps = new ArrayList<Double>();
    private static String Videoname;

    public static void main(String[] args) throws Exception {
        createWindow(titelWindow, width, height);

    }

    static void ToAudio(String from, String to) {
        if (from == null) {
            Error();
        } else {

            IMediaReader mediaReader = ToolFactory.makeReader(from);
            final int mySampleRate = 44100;
            final int myChannels = 2;

            mediaReader.addListener(new MediaToolAdapter() {

                private IContainer container;
                private IMediaWriter mediaWriter;

                @Override
                public void onOpenCoder(IOpenCoderEvent event) {
                    container = event.getSource().getContainer();
                    mediaWriter = null;
                }

                @Override
                public void onAudioSamples(IAudioSamplesEvent event) {
                    if (container != null) {
                        if (mediaWriter == null) {
                            mediaWriter = ToolFactory.makeWriter(to);

                            mediaWriter.addListener(new MediaListenerAdapter() {

                                @Override
                                public void onAddStream(IAddStreamEvent event) {
                                    IStreamCoder streamCoder = event.getSource().getContainer().getStream(event.getStreamIndex()).getStreamCoder();
                                    streamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, false);
                                    streamCoder.setBitRate(128);
                                    streamCoder.setChannels(myChannels);
                                    streamCoder.setSampleRate(mySampleRate);
                                    streamCoder.setBitRateTolerance(0);

                                }
                            });

                            mediaWriter.addAudioStream(0, 0, myChannels, mySampleRate);
                        }
                        mediaWriter.encodeAudio(0, event.getAudioSamples());
                        //System.out.println(event.getTimeStamp() / 1000);
                    }
                }

                @Override
                public void onClose(ICloseEvent event) {
                    if (mediaWriter != null) {
                        mediaWriter.close();
                    }
                }
            });

            while (mediaReader.readPacket() == null) {
            }
            ConvertedToAudio = true;
        }

    }

    public static void createWindow(String title, int width, int height) throws IOException {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JButton button = new JButton("Pick videofootage");
        JButton button2 = new JButton("ToAudio");
        JButton button3 = new JButton("Edit Video");
        JButton button4 = new JButton("BGM Off");
        button.setBackground(new Color(0, 130, 252));
        button2.setBackground(new Color(0, 160, 252));
        button3.setBackground(new Color(0, 190, 252));
        button4.setBackground(new Color(0, 190, 252));
        try {
            Image img = ImageIO.read(new File(path));
            Image image2 = Toolkit.getDefaultToolkit().createImage(path2);
            Image image3 = Toolkit.getDefaultToolkit().createImage(path3);

            JPanel panel = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(img, 0, 0, null);
                    g.drawImage(image2, 100, 150, this);
                    g.drawImage(image3, 395, -55, this);

                }
            };

            panel.add(button);
            panel.add(button2);
            panel.add(button3);
            panel.add(button4);
            frame.add(panel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);

        button4.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {

                if (ButtonOn == true) {
                    button4.setText("BGM Off");
                    ButtonOn = false;

                } else if (ButtonOn == false) {
                    button4.setText("BGM On");
                    ButtonOn = true;

                }

                playMusic(path4, ButtonOn);

            }

        });
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser(".");
                fileChooser.setControlButtonsAreShown(false);
                frame.add(fileChooser, BorderLayout.CENTER);
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Photo and video", "jpg", "gif", "mp4", "png");
                fileChooser.setFileFilter(filter);
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    Videofile = fileChooser.getSelectedFile().getPath();
                    System.out.println(Videofile);
                    pickedVideo = true;
                    Videoname = fileChooser.getSelectedFile().getName();
                }

            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (pickedVideo == true) {

                    JFrame f = new JFrame("Loading...");
                    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    JPanel p = new JPanel();

                    JProgressBar pb = new JProgressBar(0, 100);
                    pb.setValue(0);
                    pb.setStringPainted(true);
                    p.add(pb);
                    f.add(p);
                    f.setSize(300, 150);
                    f.setVisible(true);

                    int i = 0;

                    try {
                        while (i <= 100) {
                            // fill the menu bar
                            pb.setValue(i + 10);

                            // delay the thread
                            Thread.sleep(150);

                            i += 20;
                        }

                        pb.setString("Done!");
                    } catch (InterruptedException ie) {

                    }
                    ToAudio(Videofile, ToAudio);
                } else {
                    Error();
                }

            }

        });

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (pickedVideo == true && ConvertedToAudio == true) {
                    try {
                        AnalyzeAudio();
                    } catch (Exception ex) {
                        Logger.getLogger(Autoeditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    Error2();
                }

            }
        });
    }

    static void playMusic(String musicLocation, boolean Button) {
        File musicPath = new File(musicLocation);

        try {
            if (AlreadyPlaying == false) {

                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                AlreadyPlaying = true;
            }
            if (musicPath.exists()) {

                clip.loop(Clip.LOOP_CONTINUOUSLY);
                if (Button == true) {
                    clip.setMicrosecondPosition(clipTime);
                    clip.start();
                } else if (Button == false) {
                    clipTime = clip.getMicrosecondPosition();
                    clip.stop();

                }

            } else {
                System.out.println("File not founded");
            }

        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
        }

    }

    static void Error() {

        JFrame f = new JFrame();
        JOptionPane.showMessageDialog(f, "Pick videofootage!", "Alert", JOptionPane.WARNING_MESSAGE);

    }

    static void Error2() {

        JFrame f = new JFrame();
        JOptionPane.showMessageDialog(f, "Wasn’t converted to audio yet!", "Alert", JOptionPane.WARNING_MESSAGE);

    }

    static void AnalyzeAudio() throws Exception {

        String SilenceTimeStamps = "&& ffmpeg -i Audio.mp3 -af silencedetect=noise=-30dB:d=1 -f null -";
        String LengthOfVideo = "&& ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 " + Videoname;
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/C", "cd /d\"C:\\Autoeditor\" && dir " + SilenceTimeStamps + LengthOfVideo);

        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        String Length = "";
        while ((line = r.readLine()) != null) {
            int SilenceStart = line.indexOf("silence_start:");
            int SilenceEnd = line.indexOf("silence_end:");
            int LengthVid = line.indexOf("silence_end:");
            if (SilenceStart >= 0) {
                String start = line.substring(SilenceStart + "silence_start:".length(), line.length()).trim();
                GetSilenceTimeStamps.add(Double.parseDouble(start));

            }
            if (SilenceEnd >= 0) {
                String end = line.substring(SilenceEnd + "silence_end:".length(), line.length() - 27).trim(); // -27 to remove the what’s left
                GetSilenceTimeStamps.add(Double.parseDouble(end));

            }

            System.out.println(GetSilenceTimeStamps);
            System.out.println(line);
            Length = line;
        }

        EditVideo(Length);
    }

    static void EditVideo(String Duration) throws IOException {

        boolean silence = false;
        double time = 0;
        int i = 0;
        for (i = 0; i < GetSilenceTimeStamps.size(); i++) {
            String count = "" + i;
            if (silence == true) {
                time += GetSilenceTimeStamps.get(i) - time;
                silence = false;
            }
            if (silence == false) {

                System.out.println(GetSilenceTimeStamps.get(i));
                cut(count, time, GetSilenceTimeStamps.get(i) - time);
                time += GetSilenceTimeStamps.get(i) - time;
                silence = true;
            }

        }
        if (Double.parseDouble(Duration) - time != 0) {
            i++;
            String s = "" + i;
            cut(s, time, Double.parseDouble(Duration) - time);
           
        }
    Concentrate(i);
    }

    static void cut(String count, double startVideoCut, double endVideoCut) throws IOException {

        String s = "&& ffmpeg -ss " + String.valueOf(startVideoCut) + " -i " + Videoname + " -t " + String.valueOf(endVideoCut) + " -c copy" + " clip" + count + ".mp4";
        System.out.println(s);
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/C", "cd /d\"C:\\Autoeditor\" && dir " + s);
//ffmpeg -ss [start] -i in.mp4 -t [duration] -c copy out.mp4

        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = r.readLine()) != null) {

            System.out.println(line);
        }
      
    }

    static void Concentrate(int n) throws IOException {
        //ffmpeg -safe 0 -f concat -i list.txt -c copy output.mp4
        //  ffmpeg -i "concat:input1.mp4|input2.mp4|input3.mp4" -c copy output.mp4
        FileWriter myWriter = new FileWriter(new File("C:\\Autoeditor\\path.txt"));
        for (int i = 0; i <= n; i++) {
            String o = "" + i;
            String s = "file 'clip" + o + ".mp4" + "'" + "\n";
            myWriter.write(s);

        }
        myWriter.close();
        String concentrate = " && ffmpeg -f concat -i path.txt -c copy EditedVideo.mp4";
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/C", "cd /d\"C:\\Autoeditor\" && dir " + concentrate);

        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = r.readLine()) != null) {

            System.out.println(line);
        }

    }

}
