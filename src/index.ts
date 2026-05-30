import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import dotenv from 'dotenv';
import authRoutes from './routes/auth';
import contactRoutes from './routes/contact';

dotenv.config();

const app = express();
const port = process.env.PORT || 5001;

app.use(cors());
app.use(helmet());
app.use(express.json());

// Request logging middleware
app.use((req, res, next) => {
  console.log(`${req.method} ${req.url}`);
  next();
});

// Routes
app.use('/api/v1/auth', authRoutes);
app.use('/api/v1/contact', contactRoutes);
app.use('/api/v1/projects', (req, res) => res.json({ data: [] }));
app.use('/api/v1/services', (req, res) => res.json({ data: [] }));

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'UP', service: 'agency-node-backend' });
});

// Error handling middleware
app.use((err: any, req: express.Request, res: express.Response, next: express.NextFunction) => {
  console.error(err.stack);
  res.status(err.status || 500).json({ message: err.message || 'Internal Server Error' });
});

app.listen(port, () => {
  console.log(`[INFO] Node backend started on port ${port}`);
});
