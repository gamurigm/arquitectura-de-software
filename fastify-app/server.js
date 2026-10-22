import Fastify from 'fastify';
import fastifyStatic from '@fastify/static';
import { fileURLToPath } from 'node:url';

const app = Fastify({ logger: true });
const publicDirectory = fileURLToPath(new URL('./public/', import.meta.url));

await app.register(fastifyStatic, { root: publicDirectory });

const port = Number(process.env.PORT || 3000);

try {
  await app.listen({ host: '127.0.0.1', port });
} catch (error) {
  app.log.error(error);
  process.exit(1);
}
