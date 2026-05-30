import { Router } from 'express';
import { PrismaClient } from '@prisma/client';
import { z } from 'zod';
import nodemailer from 'nodemailer';

const router = Router();
const prisma = new PrismaClient();

const transporter = nodemailer.createTransport({
  host: process.env.MAIL_HOST || 'smtp.zoho.in',
  port: parseInt(process.env.MAIL_PORT || '587'),
  secure: false, // true for 465, false for other ports
  auth: {
    user: process.env.MAIL_USERNAME,
    pass: process.env.MAIL_PASSWORD,
  },
});

const contactSchema = z.object({
  name: z.string().min(2),
  email: z.string().email(),
  phone: z.string().optional(),
  service: z.string().min(1),
  message: z.string().min(1),
});

router.post('/', async (req, res) => {
  try {
    const data = contactSchema.parse(req.body);
    
    const submission = await prisma.contactSubmission.create({
      data: {
        name: data.name,
        email: data.email,
        phone: data.phone || '',
        service: data.service,
        message: data.message,
      }
    });

    if (process.env.MAIL_USERNAME && process.env.ADMIN_EMAIL) {
      const mailOptions = {
        from: process.env.MAIL_USERNAME,
        to: process.env.ADMIN_EMAIL,
        subject: `New Agency Lead: ${data.name}`,
        text: `New contact submission received!
        
Name: ${data.name}
Email: ${data.email}
Phone: ${data.phone || 'N/A'}
Service Interest: ${data.service}

Message:
${data.message}
`
      };
      
      try {
        await transporter.sendMail(mailOptions);
        console.log('Email notification sent successfully');
      } catch (mailError) {
        console.error('Failed to send email notification:', mailError);
      }
    }

    res.status(201).json({ message: 'Message sent successfully!', data: submission });
  } catch (error) {
    console.error('Contact submission error:', error);
    res.status(400).json({ message: 'Validation failed or server error' });
  }
});

export default router;
