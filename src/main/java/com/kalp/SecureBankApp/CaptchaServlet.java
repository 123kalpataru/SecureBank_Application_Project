package com.kalp.SecureBankApp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
@WebServlet("/captcha")
public class CaptchaServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
	{
		int  width= 150, height = 40;
		
		String captchaStr = CaptchaGenerator.generatorCaptchaText(6);
		HttpSession session = req.getSession();
		
		session.setAttribute("captcha", captchaStr);
		
		BufferedImage bi = new BufferedImage( width,height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		
		g2d.setFont(new Font("Ariel", Font.BOLD,28));
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.BLACK);
		g2d.drawString(captchaStr, 20, 38);
		g2d.dispose();
		
		resp.setContentType("image/png");
		
		ImageIO.write(bi, "png", resp.getOutputStream());
		
	}

}
