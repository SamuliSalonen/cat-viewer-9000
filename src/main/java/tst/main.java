package tst;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.*;
public class main 
{
		public static void main(String[] args) 
		{
			//UI boilerplate
	    	JFrame frame = new JFrame("Cat Viewer 9000");
	    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	frame.setSize(400, 700);
	    	frame.setLayout(new BorderLayout());

	    	JLabel imageLabel = new JLabel();
	    	imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    	frame.add(imageLabel, BorderLayout.CENTER);

	    	JButton changeImageButton = new JButton("Change Image");
	    	changeImageButton.addActionListener(e -> updateImage(imageLabel));
	    	frame.add(changeImageButton, BorderLayout.SOUTH);

	    
	    	frame.setVisible(true);

	    	updateImage(imageLabel);
		}

		//fetch image url from web application
	    static RequestResult fetchImageUrl() 
	    {
	        try 
	        {
	         
	            RequestResult response = request("https://api.thecatapi.com/v1/images/search");
	            if(!response.success)
	            {
	            	return response;
	            }
	            
	            JSONArray jsonArray = new JSONArray(response.message);
	            if (jsonArray.length() > 0) {
	                JSONObject jsonObject = jsonArray.getJSONObject(0);
	                response.message = jsonObject.getString("url");
	                return response;
	            }
	        } 
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	        return null; 
	    }
	    
	    //function to update the current image
	    static void updateImage(JLabel imageLabel) 
	    {
	    	RequestResult response = fetchImageUrl();
	    	if(response == null ||!response.success)
	    		return;
	    	
	        String url = response.message; 
	        if (url == null) 
	        {
	            System.out.println("Failed to fetch image URL.");
	            return;
	        }

	        try 
	        {
	         
	            URL imageURL = new URL(url);
	            BufferedImage originalImage = ImageIO.read(imageURL);

	         
	            int maxWidth = 300;  
	            int maxHeight = 400; 
	            int width = originalImage.getWidth();
	            int height = originalImage.getHeight();

	            if (width > maxWidth || height > maxHeight) 
	            {
	               
	                double widthScale = (double) maxWidth / width;
	                double heightScale = (double) maxHeight / height;
	                double scale = Math.min(widthScale, heightScale);

	          
	                width = (int) (width * scale);
	                height = (int) (height * scale);

	          
	                Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

	             
	                imageLabel.setIcon(new ImageIcon(scaledImage));
	            } 
	            else 
	            {
	              
	                imageLabel.setIcon(new ImageIcon(originalImage));
	            }
	        } 
	        
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	    }
	    
	    //function to make a web request that returns json data, request result.success tells if the request was succesfully parsed or not
	    static RequestResult request(String urlString) 
	    {
	        HttpURLConnection connection = null;

	        try 
	        {
	            URL url = new URL(urlString);
	            connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestMethod("GET");
	            connection.setConnectTimeout(5000);
	            connection.setReadTimeout(5000);

	            int responseCode = connection.getResponseCode();

	            if (responseCode == HttpURLConnection.HTTP_OK) 
	            {
	                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                StringBuilder response = new StringBuilder();
	                String inputLine;
	                while ((inputLine = in.readLine()) != null) 
	                {
	                    response.append(inputLine);
	                }
	                in.close();
	                return new RequestResult(true, response.toString());
	            } 
	            else 
	            {
	                return new RequestResult(false, "GET request failed with response code: " + responseCode);
	            }
	        } 
	        
	        catch (Exception e) 
	        {
	            return new RequestResult(false, "Error: " + e.getMessage());
	        } 
	        finally
	        {
	            if (connection != null) 
	            {
	                connection.disconnect();
	            }
	        }
	    }
	
}
