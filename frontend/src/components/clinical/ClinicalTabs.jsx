import React, { useEffect, useState } from 'react';
// Simple tabs implemented locally to avoid external dependency
import { Card, CardHeader, CardTitle, CardContent } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import api from '../../api/api';
import { endpoints } from '../../api/endpoints';

const Empty = ({ text }) => (
  <Card className="mt-4">
    <CardContent className="p-8 text-center text-muted-foreground">{text}</CardContent>
  </Card>
);

export default function ClinicalTabs({ recordId }) {
  const [diagnoses, setDiagnoses] = useState([]);
  const [treatments, setTreatments] = useState([]);
  const [prescriptions, setPrescriptions] = useState([]);
  const [activeTab, setActiveTab] = useState('diagnoses');

  const [dxForm, setDxForm] = useState({ code: '', description: '' });
  const [txForm, setTxForm] = useState({ type: '', details: '' });
  const [rxForm, setRxForm] = useState({ drugName: '', dosage: '', frequency: '', durationDays: '' });

  useEffect(() => {
    if (!recordId) return;
    loadAll();
  }, [recordId]);

  async function loadAll() {
    const [d, t, p] = await Promise.all([
      api.get(endpoints.listDiagnoses(recordId)).then(r => r.data).catch(() => []),
      api.get(endpoints.listTreatments(recordId)).then(r => r.data).catch(() => []),
      api.get(endpoints.listPrescriptions(recordId)).then(r => r.data).catch(() => []),
    ]);
    setDiagnoses(d);
    setTreatments(t);
    setPrescriptions(p);
  }

  const addDiagnosis = async () => {
    if (!dxForm.code || !dxForm.description) return;
    await api.post(endpoints.addDiagnosis(recordId), { code: dxForm.code, description: dxForm.description });
    setDxForm({ code: '', description: '' });
    loadAll();
  };

  const addTreatment = async () => {
    if (!txForm.type || !txForm.details) return;
    await api.post(endpoints.addTreatment(recordId), { type: txForm.type, details: txForm.details });
    setTxForm({ type: '', details: '' });
    loadAll();
  };

  const addPrescription = async () => {
    if (!rxForm.drugName || !rxForm.dosage || !rxForm.frequency || !rxForm.durationDays) return;
    await api.post(endpoints.addPrescription(recordId), {
      drugName: rxForm.drugName,
      dosage: rxForm.dosage,
      frequency: rxForm.frequency,
      durationDays: Number(rxForm.durationDays),
    });
    setRxForm({ drugName: '', dosage: '', frequency: '', durationDays: '' });
    loadAll();
  };

  return (
    <div>
      <div className="inline-flex border rounded overflow-hidden">
        <button className={`px-4 py-2 ${activeTab==='diagnoses' ? 'bg-primary text-white' : 'bg-background'}`} onClick={()=>setActiveTab('diagnoses')}>Diagnoses</button>
        <button className={`px-4 py-2 ${activeTab==='treatments' ? 'bg-primary text-white' : 'bg-background'}`} onClick={()=>setActiveTab('treatments')}>Treatments</button>
        <button className={`px-4 py-2 ${activeTab==='prescriptions' ? 'bg-primary text-white' : 'bg-background'}`} onClick={()=>setActiveTab('prescriptions')}>Prescriptions</button>
      </div>

      {activeTab==='diagnoses' && (
        <>
          <Card className="mt-4">
            <CardHeader>
              <CardTitle>Add Diagnosis</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                <Input placeholder="ICD-10 Code" value={dxForm.code} onChange={(e)=>setDxForm(v=>({...v,code:e.target.value}))} />
                <Input placeholder="Description" value={dxForm.description} onChange={(e)=>setDxForm(v=>({...v,description:e.target.value}))} />
              </div>
              <Button onClick={addDiagnosis}>Add Diagnosis</Button>
            </CardContent>
          </Card>
          {diagnoses.length===0 ? <Empty text="No diagnoses yet"/> : (
            <ul className="mt-4 space-y-2">
              {diagnoses.map(d=> (
                <li key={d.diagnosisId} className="p-3 border rounded">
                  <div className="font-medium">{d.code} - {d.description}</div>
                </li>
              ))}
            </ul>
          )}
        </>
      )}

      {activeTab==='treatments' && (
        <>
          <Card className="mt-4">
            <CardHeader>
              <CardTitle>Add Treatment</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                <Input placeholder="Type" value={txForm.type} onChange={(e)=>setTxForm(v=>({...v,type:e.target.value}))} />
                <Input placeholder="Details" value={txForm.details} onChange={(e)=>setTxForm(v=>({...v,details:e.target.value}))} />
              </div>
              <Button onClick={addTreatment}>Add Treatment</Button>
            </CardContent>
          </Card>
          {treatments.length===0 ? <Empty text="No treatments yet"/> : (
            <ul className="mt-4 space-y-2">
              {treatments.map(t=> (
                <li key={t.treatmentId} className="p-3 border rounded">
                  <div className="font-medium">{t.type} - {t.details}</div>
                </li>
              ))}
            </ul>
          )}
        </>
      )}

      {activeTab==='prescriptions' && (
        <>
          <Card className="mt-4">
            <CardHeader>
              <CardTitle>Add Prescription</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="grid grid-cols-1 md:grid-cols-4 gap-2">
                <Input placeholder="Medication" value={rxForm.drugName} onChange={(e)=>setRxForm(v=>({...v,drugName:e.target.value}))} />
                <Input placeholder="Dosage" value={rxForm.dosage} onChange={(e)=>setRxForm(v=>({...v,dosage:e.target.value}))} />
                <Input placeholder="Frequency" value={rxForm.frequency} onChange={(e)=>setRxForm(v=>({...v,frequency:e.target.value}))} />
                <Input placeholder="Days" value={rxForm.durationDays} onChange={(e)=>setRxForm(v=>({...v,durationDays:e.target.value}))} />
              </div>
              <Button onClick={addPrescription}>Add Prescription</Button>
            </CardContent>
          </Card>
          {prescriptions.length===0 ? <Empty text="No prescriptions yet"/> : (
            <ul className="mt-4 space-y-2">
              {prescriptions.map(p=> (
                <li key={p.prescriptionId} className="p-3 border rounded">
                  <div className="font-medium">{p.drugName} - {p.dosage} - {p.frequency}</div>
                </li>
              ))}
            </ul>
          )}
        </>
      )}
    </div>
  );
}


