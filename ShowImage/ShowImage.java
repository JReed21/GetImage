package ShowImage;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.io.*;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Frame;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.Object;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.border.*;
import javax.swing.BoxLayout;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.*;
import com.google.gson.*;
import com.google.gson.Gson;

/**
 * @author      Joshua Reed
 * @version     1
 * @since       1
 *
 *
 *
 */

public class ShowImage extends JFrame  {
    /**
     *  The border colors to highlight JLabels when being selected
     *  Go to {@link #addListener(String photoUrl)} to see how it is implemented
     */
    Border redBorder = BorderFactory.createLineBorder(Color.RED,3);
    Border noBorder = BorderFactory.createLineBorder(Color.WHITE,0);
    String API_KEY = "5cdc3956dae1f3fc610af0d2491f1bf3";
    /**
     *  boolean to mark whether or not a JLabel has been selected
     *  Go to {@link #addListener(String photoUrl)} to see how it is implemented
     */
    Boolean hasLast = false;
    /**
     *  Array List that holds custom class PhotoData. PhotoData holds a reference to
     *  a JLabel and the url that created its image.
     */
    ArrayList<PhotoData> photosToSave;
    
    /**
     *  searchTagField is the text box field where user inputs tags to search for
     */
    JTextField searchTagField = new JTextField("");
    /**
     *  numResultsStr is the text box field where user inputs the number of results to
     *  search for
     */
    JTextField numResultsStr = new JTextField("10");
    /**
     *  onePanel holds images that will be processed
     */
    JPanel onePanel;
    /**
     *  oneScrollPanel holds onePanel
     */
    JScrollPane oneScrollPanel;
    /**
     *  previous references the previous JLabel that has been selected
     *  Go to {@link #addListener(String photoUrl)} to see how it is implemented
     */
    PhotoData previous;
    /**
     *  testButton is a JButton added to onePanel
     */
    final JButton testButton =  new JButton("Test");
    /**
     *  searchButton is a JButton added to onePanel
     */
    final JButton searchButton = new JButton("Search");
    /**
     *  saveButton is a JButton added to onePanel
     */
    final JButton saveButton = new JButton("Save");
    /**
     *  loadButton is a JButton added to onePanel
     */
    final JButton loadButton = new JButton("Load");
    /**
     *  deleteButton is a JButton added to onePanel
     */
    final JButton deleteButton = new JButton("Delete");

    static int frameWidth = 800;
    static int frameHeight = 600;
    /**
     *  Constructor method
     *
     *  This method initializes all JButton's with anonymous actionlisteners as well
     *  initializing all JPanels and JLabels for the User Interface
     *
     */
    public ShowImage() {
        // initialize previous and photosToSave
        previous = new PhotoData();
        photosToSave = new ArrayList<PhotoData>();
        
        //add ActionListener's to all buttons
        
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testButtonClicked(e);
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    searchButtonClicked(e);
                }
                catch(Exception ex) {
                    
                }
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveButtonClicked(e);
            }
        });
        
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadButtonClicked(e);
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteButtonClicked(e);
            }
        });

        // create bottom subpanel with buttons, flow layout
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        
        // add testButton to bottom subpanel
        buttonsPanel.add(testButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(loadButton);
        buttonsPanel.add(deleteButton);

        // create middle subpanel with 2 text fields and button, border layout
        JPanel textFieldSubPanel = new JPanel(new FlowLayout());
        // create and add label to subpanel
        JLabel tl = new JLabel("Enter search tag:");
        textFieldSubPanel.add(tl);

        // set width of left text field
        searchTagField.setColumns(23);
        // add left text field to middle subpanel
        textFieldSubPanel.add(searchTagField);
        // add search button to middle subpanel
        textFieldSubPanel.add(searchButton);
        // add listener for searchButton clicks

        // create and add label to middle subpanel, add to middle subpanel
        JLabel tNum = new JLabel("max search results:");
        numResultsStr.setColumns(2);
        textFieldSubPanel.add(tNum);
        textFieldSubPanel.add(numResultsStr);

        // create and add panel to contain bottom and middle subpanels
        JPanel textFieldPanel = new JPanel();
        textFieldPanel.setLayout(new BoxLayout(textFieldPanel, BoxLayout.Y_AXIS));
        textFieldPanel.add(textFieldSubPanel);
        textFieldPanel.add(buttonsPanel);

        // create top panel
        onePanel = new JPanel();
        onePanel.setLayout(new BoxLayout(onePanel, BoxLayout.Y_AXIS));

        // create scrollable panel for top panel
        oneScrollPanel = new JScrollPane(onePanel,
				      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				      JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        oneScrollPanel.setPreferredSize(new Dimension(frameWidth, frameHeight-100));
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        // add scrollable panel to main frame
        add(oneScrollPanel);

        // add panel with buttons and textfields to main frame
        add(textFieldPanel);

        // connect updated onePanel to oneScrollPanel
        oneScrollPanel.setViewportView(onePanel);
    }
    public static void main(String [] args) throws Exception {
        int frameWidth = 800;
        int frameHeight = 600;
        ShowImage frame = new ShowImage();
        frame.setTitle("Flickr Image Search");
        frame.setSize(frameWidth, frameHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    /**
     *  Retrieves an Image from URL.
     *
     *  @param loc String that contains URL to the image that will be retrieved
     *  @exception error May catch an exception if ImageIO was unable to read the URL
     *  @return BufferedImage containing the image from the URL or null
     */
    
    BufferedImage getImageURL(String loc) {
        BufferedImage img = null;
        try {
            final URL url = new URL(loc);
	        img = ImageIO.read(url);
        } catch (Exception error) {
            System.out.println("Error loading image...");
            return null;
        }
        return img;

    }
    /**
     *  Method response to loadButton being clicked
     *
     *  Use {@link #addListener(String photoUrl)} to see how picture is added
     *
     *  @param e optional ActionEvent information
     *  @exception error Exception is raised if for example file does not exist or Buffered Reader is unable to read from file
     */
    public void loadButtonClicked(ActionEvent e) {
        
        // String that will print a list of pictures being loaded
        
        String loaded = "";
        try {
            // Read the text file line by line
            BufferedReader br = new BufferedReader(new FileReader("file.txt"));
            StringBuilder sb = new StringBuilder();
            String url = br.readLine();
            
            // While there is a line, call addListener and append link to loaded
            while (url != null) {
                loaded += (url + "\n");
                addListener(url);
                url = br.readLine();
            }
            
        }
        catch(Exception error) {
            // raise Exception on error
            System.out.println("Exception: " + error);
        }
        finally {
            // Print the list of pictures
            System.out.print("Pictures loaded: \n" + loaded);
            
            // Make sure they are visible
            onePanel.revalidate();
            onePanel.repaint();
            oneScrollPanel.setViewportView(onePanel);
        }
    }
    /**
     *  Method response to saveButton being clicked
     *
     *  @param e optional ActionEvent information
     *  @exception error IOException is raised if for example file does not exist or buffered writer is unable to read from file
     */
    public void saveButtonClicked(ActionEvent e) {
        try {
            // Create BufferedWriter to write to file.txt.
            BufferedWriter out = new BufferedWriter(new FileWriter("file.txt"));
            
            // Iterate through PhotoData and write each URL to file
            for(PhotoData p : photosToSave) {
                out.write(p.URL + "\n");
            }
            out.close();
        }
        catch(IOException error) {
            System.out.println("Exception: " + error);
        }
    }
    /**
     *  Method response to deleteButton being clicked
     *
     *  @param e optional ActionEvent information
     */
    public void deleteButtonClicked(ActionEvent e) {
        // Checking if previous JLabel has been selected
        if(hasLast) {
            // Remove picture from onePanel
            onePanel.remove(previous.picture);
            // Remove previous from photosToSave
            photosToSave.remove(previous);
            previous = null;
            hasLast = false;
            // Update Changes
            onePanel.revalidate();
            onePanel.repaint();
            oneScrollPanel.setViewportView(onePanel);
        }
    }
    /**
     *  Method response to testButton being clicked
     *  Uses {@link #isValid(String url)} to check if string is valid URL
     *  Uses {@link #addListener(String photoUrl)} to add image to panel
     *  @param e optional ActionEvent information
     */
    public void testButtonClicked(ActionEvent e) {
        // Create string to hold URL from searchTagField
        String text = searchTagField.getText();
        // Verifies that text is a valid URL
        if(isValid(text)) {
            System.out.println(text);
            addListener(text);
            onePanel.revalidate();
            onePanel.repaint();
            oneScrollPanel.setViewportView(onePanel);
        }
        else {
            System.out.println("Incorrect URL format");
        }
    }
    /**
     *  Method response to searchButton being clicked
     *  Uses {@link #addListener(String photoUrl)} to add image to panel
     *  @param e optional ActionEvent information
     *  @throws Exception Exception thrown
     */
    public void searchButtonClicked(ActionEvent e) throws Exception {
        String numResults = numResultsStr.getText();
        String text = searchTagField.getText();
        String api  = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
        
        // number of results per page
        String request = api + "&per_page=";
        // either change  number of results to given number, or set to default of 10
        request += (numResults.length() != 0) ? numResults : "10";
        request += "&format=json&nojsoncallback=1&extras=geo";
        // add API_KEY
        request += "&api_key=" + API_KEY;
        
        // replacing any spaces in searchTagField with %20
        if (text.length() != 0) {
            text = text.replaceAll(" ","%20");
            request += "&tags="+text;
        }
        // open http connection
        URL obj = new URL(request);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        // send GET request
        con.setRequestMethod("GET");
        
        // get response
        int responseCode = con.getResponseCode();
        
        System.out.println("Response Code : " + responseCode);
        
        // read and construct response String
        BufferedReader in = new BufferedReader(new InputStreamReader
                                               (con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        
        System.out.println(response);
        // Create GSON object to parse JSON
        Gson gson = new Gson();
        String s = response.toString();
        // Parse Json and create new Response object
        Response responseObject = gson.fromJson(s, Response.class);
        System.out.println("# photos = " + responseObject.photos.photo.length);
        // Iterate through each photo in responseObject
        for(PhotoInfo p : responseObject.photos.photo) {
            int farm = p.farm;
            // Create valid flickr URL
            String server = p.server;
            String id = p.id;
            String secret = p.secret;
            String photoUrl = "http://farm"+farm+".static.flickr.com/"
            +server+"/"+id+"_"+secret+".jpg";
            System.out.println(photoUrl);
            //Add image to panel and update
            addListener(photoUrl);
        }
        onePanel.revalidate();
        onePanel.repaint();
        oneScrollPanel.setViewportView(onePanel);
        

        
    }
    /**
     *  Method that adds created image to JLabel and adds an actionlistener to JLabel
     *  Uses {@link #getImageURL(String loc)} to get image from URL
     *  Uses {@link #scaleImage(Dimension originalImage, int height)} to scale image
     *  
     *  @param photoUrl contains URL to create image
     *
     */
    public void addListener(String photoUrl) {
        // Gets image from photoURL
        BufferedImage img = getImageURL(photoUrl);
        // Checks if getImageURL worked correctly
        if(img != null) {
            // Scale image to new aspect ratio with height of 200
            Dimension newDimensions = scaleImage(new Dimension((int)img.getWidth(),(int)img.getHeight()),200);
            //  Scales image to new size
            img.getScaledInstance((int)newDimensions.getWidth(), (int)newDimensions.getHeight(), Image.SCALE_DEFAULT);
            // Create new JLabel with image
            JLabel j = new JLabel(new ImageIcon(img));
            // Create new PhotoData object with url and JLabel
            PhotoData pic = new PhotoData(j,photoUrl);
            // Add a mouse Listener to select JLabel when clicked
            pic.picture.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // if previous JLabel has been selected and it's the same JLabel
                    if(hasLast && pic == previous) {
                        // deselect previous
                        previous.picture.setBorder(noBorder);
                        hasLast = false;
                        previous = null;
                    }
                    // if previous JLabel has been selected but it's a different JLabel
                    else if(hasLast && pic != previous) {
                        //deselect previous
                        previous.picture.setBorder(noBorder);
                        previous = pic;
                        //select new JLabel
                        previous.picture.setBorder(redBorder);
                        System.out.println("Selected Photo Index: " + photosToSave.indexOf(previous));
                    
                    }
                    else {
                        // select new JLabel
                        pic.picture.setBorder(redBorder);
                        hasLast = true;
                        previous = pic;
                        System.out.println("Selected Photo Index: " + photosToSave.indexOf(previous));
                    }
                }
            });
            // update panel
            photosToSave.add(pic);
            onePanel.add(pic.picture);
        }
        else {
            System.out.println("Error retrieving image");
        }
    }
    /**
     *  Method that creates new Dimensions for image
     *
     *  @param originalImage dimensions of original image
     *  @param height new height of image
     *  @return        Dimension with new height and width
     */
    public static Dimension scaleImage(Dimension originalImage, int height) {
        // get height and width of original image
        int new_height = originalImage.height;
        int new_width = originalImage.width;
        if(originalImage.height > height) {
            //create new height and width
            int original_height = originalImage.height;
            new_height = height;
            new_width = (new_height * original_height) / originalImage.width;
        }
        // return new Dimension
        return new Dimension(new_width,new_height);
    }
    /**
     *  Helper Method that uses regular expressions to check if URL is valid
     *
     *  @param url String containing URL
     *  @return     either returns true if valid or false
     */
    public boolean isValid(String url) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        try {
            Pattern patt = Pattern.compile(regex);
            Matcher matcher = patt.matcher(url);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }
}