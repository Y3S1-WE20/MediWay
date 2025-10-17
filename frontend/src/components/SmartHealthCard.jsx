import { useState } from 'react';
import { QRCodeSVG } from 'qrcode.react';
import { Download, Heart, User, Calendar, Droplet, Mail, Phone, IdCard } from 'lucide-react';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

const SmartHealthCard = ({ patient }) => {
  const [isDownloading, setIsDownloading] = useState(false);

  // Generate QR code URL with patient UUID
  const qrCodeUrl = `https://mediway.lk/patient/${patient.uuid}`;

  const handleDownloadCard = async () => {
    setIsDownloading(true);
    try {
      const cardElement = document.getElementById('health-card');
      
      // Create canvas from the card element
      const canvas = await html2canvas(cardElement, {
        scale: 2,
        backgroundColor: '#ffffff',
        logging: false,
        windowWidth: cardElement.scrollWidth,
        windowHeight: cardElement.scrollHeight,
      });

      // Convert canvas to image
      const imgData = canvas.toDataURL('image/png');

      // Calculate dimensions to fit the card properly
      const imgWidth = canvas.width;
      const imgHeight = canvas.height;
      const aspectRatio = imgWidth / imgHeight;
      
      // Create PDF with proper dimensions (A4 landscape or custom size)
      // Using a larger format to ensure the full card fits
      const pdfWidth = 200; // mm
      const pdfHeight = pdfWidth / aspectRatio;

      const pdf = new jsPDF({
        orientation: pdfWidth > pdfHeight ? 'landscape' : 'portrait',
        unit: 'mm',
        format: [pdfWidth, pdfHeight],
      });

      // Add image to PDF with proper scaling
      pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight);

      // Download PDF
      pdf.save(`${patient.name.replace(/\s+/g, '_')}_HealthCard.pdf`);
    } catch (error) {
      console.error('Error downloading card:', error);
      alert('Failed to download card. Please try again.');
    } finally {
      setIsDownloading(false);
    }
  };

  return (
  <div style={{ width: '100%', maxWidth: '64rem', margin: '0 auto', padding: '1.5rem' }}>
      {/* Health Card */}
      <div
        id="health-card"
        style={{
          position: 'relative',
          borderRadius: '1rem',
          boxShadow: '0 8px 32px 0 rgba(0,0,0,0.15)',
          overflow: 'hidden',
          transition: 'transform 0.3s, box-shadow 0.3s',
          background: 'linear-gradient(135deg, #14b8a6 0%, #0d9488 50%, #0f766e 100%)',
        }}
        onMouseEnter={e => { e.currentTarget.style.transform = 'scale(1.02)'; e.currentTarget.style.boxShadow = '0 16px 48px 0 rgba(0,0,0,0.18)'; }}
        onMouseLeave={e => { e.currentTarget.style.transform = 'scale(1)'; e.currentTarget.style.boxShadow = '0 8px 32px 0 rgba(0,0,0,0.15)'; }}
      >
        {/* Background Pattern */}
        <div style={{ position: 'absolute', inset: 0, opacity: 0.10 }}>
          <div style={{
            position: 'absolute',
            top: 0,
            right: 0,
            width: '16rem',
            height: '16rem',
            background: '#fff',
            borderRadius: '9999px',
            transform: 'translate(8rem, -8rem)'
          }}></div>
          <div style={{
            position: 'absolute',
            bottom: 0,
            left: 0,
            width: '12rem',
            height: '12rem',
            background: '#fff',
            borderRadius: '9999px',
            transform: 'translate(-6rem, 6rem)'
          }}></div>
        </div>

        {/* Card Content */}
        <div style={{ position: 'relative', padding: '2rem' }}>
          {/* Header */}
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.5rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <div style={{ background: 'rgba(255,255,255,0.20)', padding: '0.5rem', borderRadius: '0.75rem' }}>
                <Heart style={{ height: '2rem', width: '2rem', color: '#fff' }} fill="currentColor" />
              </div>
              <div>
                <h2 style={{ color: '#fff', fontWeight: 700, fontSize: '1.5rem', fontFamily: 'Poppins, sans-serif' }}>MediWay</h2>
                <p style={{ color: 'rgba(255,255,255,0.80)', fontSize: '0.875rem' }}>Smart Health Card</p>
              </div>
            </div>
            <div style={{ background: 'rgba(255,255,255,0.20)', padding: '0.5rem 1rem', borderRadius: '0.5rem' }}>
              <p style={{ color: '#fff', fontSize: '0.75rem', fontWeight: 600 }}>Sri Lanka</p>
            </div>
          </div>

          {/* Main Content Grid */}
          <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1.5rem' }}>
            {/* Patient Info - Left Column */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {/* Name */}
              <div style={{ background: 'rgba(255,255,255,0.10)', borderRadius: '0.75rem', padding: '1rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                  <User style={{ height: '1rem', width: '1rem', color: 'rgba(255,255,255,0.80)' }} />
                  <p style={{ color: 'rgba(255,255,255,0.80)', fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Patient Name</p>
                </div>
                <p style={{ color: '#fff', fontWeight: 700, fontSize: '1.25rem', fontFamily: 'Poppins, sans-serif' }}>{patient.name}</p>
              </div>

              {/* Patient Details Grid */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                {/* Date of Birth */}
                <div style={{ background: 'rgba(255,255,255,0.10)', borderRadius: '0.75rem', padding: '0.75rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                    <Calendar style={{ height: '0.75rem', width: '0.75rem', color: 'rgba(255,255,255,0.80)' }} />
                    <p style={{ color: 'rgba(255,255,255,0.80)', fontSize: '0.75rem', textTransform: 'uppercase' }}>DOB</p>
                  </div>
                  <p style={{ color: '#fff', fontWeight: 600 }}>{patient.dob}</p>
                </div>

                {/* Blood Group */}
                <div style={{ background: 'rgba(255,255,255,0.10)', borderRadius: '0.75rem', padding: '0.75rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                    <Droplet style={{ height: '0.75rem', width: '0.75rem', color: 'rgba(255,255,255,0.80)' }} />
                    <p style={{ color: 'rgba(255,255,255,0.80)', fontSize: '0.75rem', textTransform: 'uppercase' }}>Blood</p>
                  </div>
                  <p style={{ color: '#fff', fontWeight: 600 }}>{patient.bloodGroup}</p>
                </div>

                {/* Email */}
                <div style={{ background: 'rgba(255,255,255,0.10)', borderRadius: '0.75rem', padding: '0.75rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                    <Mail style={{ height: '0.75rem', width: '0.75rem', color: 'rgba(255,255,255,0.80)' }} />
                    <p style={{ color: 'rgba(255,255,255,0.80)', fontSize: '0.75rem', textTransform: 'uppercase' }}>Email</p>
                  </div>
                  <p style={{ color: '#fff', fontWeight: 600, fontSize: '0.875rem', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{patient.email}</p>
                </div>

                {/* Phone */}
                <div style={{ background: 'rgba(255,255,255,0.10)', borderRadius: '0.75rem', padding: '0.75rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                    <Phone style={{ height: '0.75rem', width: '0.75rem', color: 'rgba(255,255,255,0.80)' }} />
                    <p style={{ color: 'rgba(255,255,255,0.80)', fontSize: '0.75rem', textTransform: 'uppercase' }}>Phone</p>
                  </div>
                  <p style={{ color: '#fff', fontWeight: 600, fontSize: '0.875rem' }}>{patient.phone}</p>
                </div>
              </div>

              {/* Patient ID */}
              <div style={{ background: 'rgba(255,255,255,0.10)', borderRadius: '0.75rem', padding: '0.75rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                  <IdCard style={{ height: '0.75rem', width: '0.75rem', color: 'rgba(255,255,255,0.80)' }} />
                  <p style={{ color: 'rgba(255,255,255,0.80)', fontSize: '0.75rem', textTransform: 'uppercase' }}>Patient ID</p>
                </div>
                <p style={{ color: '#fff', fontFamily: 'monospace', fontSize: '0.875rem' }}>{patient.uuid}</p>
              </div>
            </div>

            {/* QR Code - Right Column */}
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
              <div style={{ background: '#fff', padding: '1rem', borderRadius: '1rem', boxShadow: '0 4px 24px 0 rgba(0,0,0,0.08)' }}>
                <QRCodeSVG
                  value={qrCodeUrl}
                  size={160}
                  level="H"
                  includeMargin={true}
                  fgColor="#0f766e"
                  bgColor="#ffffff"
                />
              </div>
              <p style={{ color: 'rgba(255,255,255,0.80)', fontSize: '0.75rem', textAlign: 'center', marginTop: '0.75rem' }}>Scan for patient details</p>
            </div>
          </div>

          {/* Footer */}
          <div style={{ marginTop: '1.5rem', paddingTop: '1rem', borderTop: '1px solid rgba(255,255,255,0.20)' }}>
            <p style={{ color: 'rgba(255,255,255,0.70)', fontSize: '0.75rem', textAlign: 'center' }}>
              This card is valid for identification at all MediWay partner hospitals
            </p>
          </div>
        </div>
      </div>

      {/* Download Button */}
      <div style={{ marginTop: '1.5rem', display: 'flex', justifyContent: 'center' }}>
        <button
          onClick={handleDownloadCard}
          disabled={isDownloading}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem',
            fontWeight: 600,
            padding: '0.75rem 1.5rem',
            borderRadius: '0.75rem',
            boxShadow: '0 2px 8px 0 rgba(0,0,0,0.10)',
            backgroundColor: '#0d9488',
            color: '#fff',
            border: 'none',
            cursor: isDownloading ? 'not-allowed' : 'pointer',
            opacity: isDownloading ? 0.5 : 1,
            transition: 'background 0.2s, box-shadow 0.2s',
          }}
          onMouseEnter={e => e.currentTarget.style.backgroundColor = '#0f766e'}
          onMouseLeave={e => e.currentTarget.style.backgroundColor = '#0d9488'}
        >
          <Download style={{ width: '1.25rem', height: '1.25rem' }} />
          {isDownloading ? 'Generating PDF...' : 'Download Health Card'}
        </button>
      </div>

      {/* Info Text */}
      <div style={{ marginTop: '1.5rem', textAlign: 'center' }}>
        <p style={{ color: '#64748b', fontSize: '0.95rem' }}>
          Your digital health card is always accessible. Download a PDF copy for offline use or print it.
        </p>
      </div>
    </div>
  );
};

export default SmartHealthCard;
