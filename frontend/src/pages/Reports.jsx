import React, { useEffect, useState } from 'react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend,
  LineChart, Line, PieChart, Pie, Cell, AreaChart, Area
} from 'recharts';
import { Button } from '../components/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';

const COLORS = ["#4CAF50", "#FF9800", "#F44336", "#2196F3", "#673AB7", "#00BCD4", "#E91E63", "#607D8B"];

const Reports = () => {
  const [dashboardStats, setDashboardStats] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const statsResponse = await fetch('http://localhost:8080/reports/dashboard');
      const statsData = await statsResponse.json();
        setDashboardStats(statsData);
      } catch (e) { setDashboardStats({}); }
      setLoading(false);
    };

    const handleDownload = (type) => {
      const url = type === 'pdf'
        ? 'http://localhost:8080/reports/summary/pdf'
        : 'http://localhost:8080/reports/summary/csv';
      fetch(url, { method: 'GET' })
        .then(res => {
          if (!res.ok) throw new Error('Download failed');
          return res.blob();
        })
        .then(blob => {
          const link = document.createElement('a');
          link.href = window.URL.createObjectURL(blob);
          link.download = type === 'pdf' ? 'hospital_report.pdf' : 'hospital_report.csv';
          document.body.appendChild(link);
          link.click();
          link.remove();
        })
        .catch(() => alert('Failed to download report.'));
    };

    if (loading) return <div className="p-8 text-center">Loading reports...</div>;

    return (
      <div className="max-w-6xl mx-auto py-8 space-y-8">
        <div className="flex gap-4 justify-end">
          <Button onClick={() => handleDownload('pdf')} className="bg-[#4CAF50] text-white">Download PDF</Button>
          <Button onClick={() => handleDownload('csv')} variant="outline">Download CSV</Button>
        </div>

        {/* 1. Patient–Doctor–Appointment Overview (Bar Chart) */}
        <Card>
          <CardHeader><CardTitle>🩺 Patient–Doctor–Appointment Overview</CardTitle></CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={220}>
              <BarChart
                data={[
                  { name: 'Patients', count: dashboardStats.patientsCount || 0 },
                  { name: 'Doctors', count: dashboardStats.doctorsCount || 0 },
                  { name: 'Appointments', count: dashboardStats.appointmentsCount || 0 }
                ]}
                margin={{ top: 20, right: 30, left: 0, bottom: 5 }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Legend />
                <Bar dataKey="count" fill="#4CAF50" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* 2. Monthly Revenue (Line / Bar Chart) */}
        <Card>
          <CardHeader><CardTitle>💰 Monthly Revenue</CardTitle></CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={220}>
              <LineChart data={dashboardStats.monthlyRevenue || []}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="revenue" stroke="#4CAF50" />
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* 3. Payment Status Distribution (Pie / Donut Chart) */}
        <Card>
          <CardHeader><CardTitle>✅ Payment Status Distribution</CardTitle></CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={220}>
              <PieChart>
                <Pie
                  data={Object.entries(dashboardStats.paymentsByStatus || {}).map(([status, value]) => ({ name: status, value }))}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={80}
                  fill="#8884d8"
                  label
                >
                  {Object.entries(dashboardStats.paymentsByStatus || {}).map((entry, idx) => (
                    <Cell key={`cell-${idx}`} fill={COLORS[idx % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* 4. Appointments by Department / Specialization (Bar Chart) */}
        <Card>
          <CardHeader><CardTitle>🏥 Appointments by Department</CardTitle></CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={220}>
              <BarChart
                layout="vertical"
                data={dashboardStats.appointmentsByDepartment || []}
                margin={{ top: 20, right: 30, left: 0, bottom: 5 }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis type="number" allowDecimals={false} />
                <YAxis dataKey="department" type="category" />
                <Tooltip />
                <Legend />
                <Bar dataKey="count" fill="#2196F3" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* 5. Active vs Inactive Users (Pie Chart) */}
        <Card>
          <CardHeader><CardTitle>👥 Active vs Inactive Users</CardTitle></CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={220}>
              <PieChart>
                <Pie
                  data={dashboardStats.usersByStatus ? Object.entries(dashboardStats.usersByStatus).map(([status, value]) => ({ name: status, value })) : []}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={80}
                  fill="#8884d8"
                  label
                >
                  {dashboardStats.usersByStatus && Object.entries(dashboardStats.usersByStatus).map((entry, idx) => (
                    <Cell key={`cell-user-${idx}`} fill={COLORS[idx % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* 6. Daily Appointments Trend (Area Chart) */}
        <Card>
          <CardHeader><CardTitle>📆 Daily Appointments Trend</CardTitle></CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={220}>
              <AreaChart data={dashboardStats.dailyAppointments || []}>
                <defs>
                  <linearGradient id="colorAppt" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#4CAF50" stopOpacity={0.8}/>
                    <stop offset="95%" stopColor="#4CAF50" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <XAxis dataKey="date" />
                <YAxis />
                <CartesianGrid strokeDasharray="3 3" />
                <Tooltip />
                <Area type="monotone" dataKey="count" stroke="#4CAF50" fillOpacity={1} fill="url(#colorAppt)" />
              </AreaChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>



        {/* 10. Pending Payments Over Time (Line Chart) */}
        <Card>
          <CardHeader><CardTitle>⏱️ Pending Payments Over Time</CardTitle></CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={220}>
              <LineChart data={dashboardStats.pendingPaymentsOverTime || []}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="pendingCount" stroke="#F44336" />
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>
    );
  };

  export default Reports;
