import { Router } from 'express';
import { PrismaClient } from '@prisma/client';
import { z } from 'zod';
import { resend } from '../utils/resend';

const router = Router();
const prisma = new PrismaClient();

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

    if (process.env.RESEND_API_KEY && process.env.ADMIN_EMAIL) {
      try {
        await resend.emails.send({
          from: 'onboarding@resend.dev',
          to: process.env.ADMIN_EMAIL,
          subject: `New Contact Form Submission - ${data.service}`,
          html: `
            <h2>New Client Inquiry</h2>
            <p><strong>Name:</strong> ${data.name}</p>
            <p><strong>Email:</strong> ${data.email}</p>
            <p><strong>WhatsApp:</strong> ${data.phone}</p>
            <p><strong>Service:</strong> ${data.service}</p>
            <p><strong>Message:</strong></p>
            <p>${data.message}</p>
          `,
        });
        console.log('Email notification sent successfully via Resend');
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
