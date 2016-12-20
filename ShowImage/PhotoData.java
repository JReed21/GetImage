package ShowImage;
import java.util.*;
import javax.swing.JFrame;
import java.awt.*;
import java.io.IOException;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;

class PhotoData {
    
    JLabel picture;
    String URL;
    
    PhotoData() {
        
    }
    PhotoData(JLabel label, String url) {
        picture = label;
        URL = url;
    }
}