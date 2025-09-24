// Seed script for mongosh. Run with:
//   mongosh "mongodb://root:secret@localhost:27017/franchise_db?authSource=admin" scripts/seed-mongosh.js

const dbName = 'franchise_db';
const dbRef = db.getSiblingDB(dbName);

print(`Using DB: ${dbName}`);

const franchises = dbRef.getCollection('franchises');
const branches = dbRef.getCollection('branches');
const products = dbRef.getCollection('products');

franchises.deleteMany({});
branches.deleteMany({});
products.deleteMany({});

const franchiseId = (new ObjectId()).toString();
franchises.insertOne({ _id: franchiseId, name: 'ACME' });

const branch1Id = (new ObjectId()).toString();
const branch2Id = (new ObjectId()).toString();
branches.insertMany([
  { _id: branch1Id, franchiseId: franchiseId, name: 'Sucursal Centro' },
  { _id: branch2Id, franchiseId: franchiseId, name: 'Sucursal Norte' },
]);

products.insertMany([
  { _id: (new ObjectId()).toString(), branchId: branch1Id, name: 'Café', stock: 50 },
  { _id: (new ObjectId()).toString(), branchId: branch1Id, name: 'Té', stock: 75 },
  { _id: (new ObjectId()).toString(), branchId: branch2Id, name: 'Jugo', stock: 40 },
]);

print('Seed completed');
