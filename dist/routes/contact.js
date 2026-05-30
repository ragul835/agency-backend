"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const client_1 = require("@prisma/client");
const zod_1 = require("zod");
const router = (0, express_1.Router)();
const prisma = new client_1.PrismaClient();
const contactSchema = zod_1.z.object({
    name: zod_1.z.string().min(2),
    email: zod_1.z.string().email(),
    phone: zod_1.z.string().optional(),
    service: zod_1.z.string().min(1),
    message: zod_1.z.string().min(1),
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
    }
    catch (error) {
        console.error('Contact submission error:', error);
        res.status(400).json({ message: 'Validation failed or server error' });
    }
});
exports.default = router;
