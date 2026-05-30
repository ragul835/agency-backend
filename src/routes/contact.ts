import { Router } from 'express';
import { PrismaClient } from '@prisma/client';
import { z } from 'zod';

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

    res.status(201).json({ message: 'Message sent successfully!', data: submission });
  } catch (error) {
    console.error('Contact submission error:', error);
    res.status(400).json({ message: 'Validation failed or server error' });
  }
});

export default router;
