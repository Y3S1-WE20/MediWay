import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import MedicalRecordList from '../MedicalRecordList';

// Mock data
const mockRecords = [
  {
    recordId: '123e4567-e89b-12d3-a456-426614174000',
    patientId: '123e4567-e89b-12d3-a456-426614174001',
    patientName: 'John Doe',
    doctorId: '123e4567-e89b-12d3-a456-426614174002',
    doctorName: 'Dr. Jane Smith',
    diagnosis: 'Hypertension',
    medications: 'Lisinopril 10mg daily',
    notes: 'Patient shows improvement',
    createdAt: '2024-01-15T10:30:00Z',
    updatedAt: '2024-01-15T10:30:00Z',
  },
  {
    recordId: '123e4567-e89b-12d3-a456-426614174003',
    patientId: '123e4567-e89b-12d3-a456-426614174001',
    patientName: 'John Doe',
    doctorId: '123e4567-e89b-12d3-a456-426614174002',
    doctorName: 'Dr. Jane Smith',
    diagnosis: 'Diabetes Type 2',
    medications: 'Metformin 500mg twice daily',
    notes: 'Blood sugar monitoring required',
    createdAt: '2024-01-14T14:20:00Z',
    updatedAt: '2024-01-14T16:45:00Z',
  },
];

const mockEmptyRecords = [];
const mockSingleRecord = [mockRecords[0]];

describe('MedicalRecordList', () => {
  const defaultProps = {
    records: mockRecords,
    loading: false,
    error: null,
    userRole: 'DOCTOR',
  };

  describe('Rendering', () => {
    it('should render medical records list correctly', () => {
      render(<MedicalRecordList {...defaultProps} />);
      
      expect(screen.getByText('Hypertension')).toBeInTheDocument();
      expect(screen.getByText('Diabetes Type 2')).toBeInTheDocument();
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('Dr. Jane Smith')).toBeInTheDocument();
    });

    it('should render loading state', () => {
      render(<MedicalRecordList {...defaultProps} loading={true} />);
      
      expect(screen.getByText('Loading medical records...')).toBeInTheDocument();
      expect(screen.getByRole('status')).toBeInTheDocument();
    });

    it('should render error state', () => {
      const errorMessage = 'Failed to load records';
      render(<MedicalRecordList {...defaultProps} error={errorMessage} />);
      
      expect(screen.getByText('Error Loading Records')).toBeInTheDocument();
      expect(screen.getByText(errorMessage)).toBeInTheDocument();
    });

    it('should render empty state for no records', () => {
      render(<MedicalRecordList {...defaultProps} records={mockEmptyRecords} />);
      
      expect(screen.getByText('No medical records yet')).toBeInTheDocument();
      expect(screen.getByText(/Start by adding a new medical record/)).toBeInTheDocument();
    });

    it('should render empty state for patient role', () => {
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={mockEmptyRecords} 
          userRole="PATIENT" 
        />
      );
      
      expect(screen.getByText('No medical records yet')).toBeInTheDocument();
      expect(screen.getByText(/Your medical records will appear here/)).toBeInTheDocument();
    });

    it('should render empty state with search query', () => {
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={mockEmptyRecords} 
          searchQuery="test" 
        />
      );
      
      expect(screen.getByText('No records found')).toBeInTheDocument();
      expect(screen.getByText(/Try adjusting your search criteria/)).toBeInTheDocument();
    });
  });

  describe('Record Display', () => {
    it('should display record information correctly', () => {
      render(<MedicalRecordList {...defaultProps} records={mockSingleRecord} />);
      
      expect(screen.getByText('Hypertension')).toBeInTheDocument();
      expect(screen.getByText('Lisinopril 10mg daily')).toBeInTheDocument();
      expect(screen.getByText('Patient shows improvement')).toBeInTheDocument();
    });

    it('should format dates correctly', () => {
      render(<MedicalRecordList {...defaultProps} records={mockSingleRecord} />);
      
      expect(screen.getByText(/Jan 15, 2024/)).toBeInTheDocument();
    });

    it('should show updated date when different from created date', () => {
      render(<MedicalRecordList {...defaultProps} records={[mockRecords[1]]} />);
      
      expect(screen.getByText(/Updated Jan 14, 2024/)).toBeInTheDocument();
    });

    it('should display record ID badge', () => {
      render(<MedicalRecordList {...defaultProps} records={mockSingleRecord} />);
      
      expect(screen.getByText('ID: 123e4567...')).toBeInTheDocument();
    });

    it('should display patient and doctor IDs', () => {
      render(<MedicalRecordList {...defaultProps} records={mockSingleRecord} />);
      
      expect(screen.getByText(/Patient ID: 123e4567\.\.\./)).toBeInTheDocument();
      expect(screen.getByText(/Doctor ID: 123e4567\.\.\./)).toBeInTheDocument();
    });
  });

  describe('Role-based Display', () => {
    it('should show patient name for doctor role', () => {
      render(<MedicalRecordList {...defaultProps} records={mockSingleRecord} />);
      
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });

    it('should show doctor name for patient role', () => {
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={mockSingleRecord} 
          userRole="PATIENT" 
        />
      );
      
      expect(screen.getByText('Dr. Jane Smith')).toBeInTheDocument();
    });

    it('should show edit and delete buttons for doctor role', () => {
      render(<MedicalRecordList {...defaultProps} records={mockSingleRecord} />);
      
      expect(screen.getAllByTestId('edit-icon')).toHaveLength(1);
      expect(screen.getAllByTestId('trash-icon')).toHaveLength(1);
    });

    it('should not show edit and delete buttons for patient role', () => {
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={mockSingleRecord} 
          userRole="PATIENT" 
        />
      );
      
      expect(screen.queryByTestId('edit-icon')).not.toBeInTheDocument();
      expect(screen.queryByTestId('trash-icon')).not.toBeInTheDocument();
    });
  });

  describe('Search Highlighting', () => {
    it('should highlight search terms in diagnosis', () => {
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={mockSingleRecord} 
          searchQuery="tension" 
        />
      );
      
      const diagnosisElement = screen.getByText(/Hypertension/);
      expect(diagnosisElement).toBeInTheDocument();
    });

    it('should highlight search terms in medications', () => {
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={mockSingleRecord} 
          searchQuery="lisinopril" 
        />
      );
      
      expect(screen.getByText(/Lisinopril 10mg daily/)).toBeInTheDocument();
    });

    it('should highlight search terms in notes', () => {
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={mockSingleRecord} 
          searchQuery="improvement" 
        />
      );
      
      expect(screen.getByText(/Patient shows improvement/)).toBeInTheDocument();
    });

    it('should handle case insensitive search', () => {
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={mockSingleRecord} 
          searchQuery="HYPERTENSION" 
        />
      );
      
      expect(screen.getByText(/Hypertension/)).toBeInTheDocument();
    });
  });

  describe('Event Handlers', () => {
    it('should call onEdit when edit button is clicked', () => {
      const mockOnEdit = jest.fn();
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={mockSingleRecord} 
          onEdit={mockOnEdit} 
        />
      );
      
      const editButton = screen.getByTestId('edit-icon').closest('button');
      fireEvent.click(editButton);
      
      expect(mockOnEdit).toHaveBeenCalledWith(mockSingleRecord[0]);
    });

    it('should call onDelete when delete button is clicked', () => {
      const mockOnDelete = jest.fn();
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={mockSingleRecord} 
          onDelete={mockOnDelete} 
        />
      );
      
      const deleteButton = screen.getByTestId('trash-icon').closest('button');
      fireEvent.click(deleteButton);
      
      expect(mockOnDelete).toHaveBeenCalledWith(mockSingleRecord[0].recordId);
    });

    it('should not call handlers when buttons are not provided', () => {
      render(<MedicalRecordList {...defaultProps} records={mockSingleRecord} />);
      
      const editButton = screen.getByTestId('edit-icon').closest('button');
      const deleteButton = screen.getByTestId('trash-icon').closest('button');
      
      expect(() => fireEvent.click(editButton)).not.toThrow();
      expect(() => fireEvent.click(deleteButton)).not.toThrow();
    });
  });

  describe('Accessibility', () => {
    it('should have proper ARIA labels for buttons', () => {
      render(<MedicalRecordList {...defaultProps} records={mockSingleRecord} />);
      
      const editButton = screen.getByTestId('edit-icon').closest('button');
      const deleteButton = screen.getByTestId('trash-icon').closest('button');
      
      expect(editButton).toHaveAttribute('title', 'Edit record');
      expect(deleteButton).toHaveAttribute('title', 'Delete record');
    });

    it('should have proper loading state accessibility', () => {
      render(<MedicalRecordList {...defaultProps} loading={true} />);
      
      const loadingSpinner = screen.getByRole('status');
      expect(loadingSpinner).toBeInTheDocument();
    });
  });

  describe('Edge Cases', () => {
    it('should handle records without medications', () => {
      const recordWithoutMedications = {
        ...mockSingleRecord[0],
        medications: null,
      };
      
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={[recordWithoutMedications]} 
        />
      );
      
      expect(screen.getByText('Hypertension')).toBeInTheDocument();
      expect(screen.queryByText('Medications')).not.toBeInTheDocument();
    });

    it('should handle records without notes', () => {
      const recordWithoutNotes = {
        ...mockSingleRecord[0],
        notes: null,
      };
      
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={[recordWithoutNotes]} 
        />
      );
      
      expect(screen.getByText('Hypertension')).toBeInTheDocument();
      expect(screen.queryByText('Notes')).not.toBeInTheDocument();
    });

    it('should handle records with empty strings', () => {
      const recordWithEmptyStrings = {
        ...mockSingleRecord[0],
        medications: '',
        notes: '',
      };
      
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={[recordWithEmptyStrings]} 
        />
      );
      
      expect(screen.getByText('Hypertension')).toBeInTheDocument();
      expect(screen.queryByText('Medications')).not.toBeInTheDocument();
      expect(screen.queryByText('Notes')).not.toBeInTheDocument();
    });

    it('should handle very long text content', () => {
      const longTextRecord = {
        ...mockSingleRecord[0],
        diagnosis: 'A'.repeat(1000),
        medications: 'B'.repeat(1000),
        notes: 'C'.repeat(1000),
      };
      
      render(
        <MedicalRecordList 
          {...defaultProps} 
          records={[longTextRecord]} 
        />
      );
      
      expect(screen.getByText(/^A+$/)).toBeInTheDocument();
      expect(screen.getByText(/^B+$/)).toBeInTheDocument();
      expect(screen.getByText(/^C+$/)).toBeInTheDocument();
    });
  });

  describe('Animation and Styling', () => {
    it('should apply hover effects to cards', () => {
      render(<MedicalRecordList {...defaultProps} records={mockSingleRecord} />);
      
      const card = screen.getByText('Hypertension').closest('[class*="hover:shadow-lg"]');
      expect(card).toBeInTheDocument();
    });

    it('should have proper spacing between records', () => {
      render(<MedicalRecordList {...defaultProps} />);
      
      const container = screen.getByText('Hypertension').closest('[class*="grid gap-6"]');
      expect(container).toBeInTheDocument();
    });
  });
});
