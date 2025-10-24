import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import MedicalRecordForm from '../MedicalRecordForm';

// Mock data
const mockPatients = [
  { patientId: '123e4567-e89b-12d3-a456-426614174001', patientName: 'John Doe' },
  { patientId: '123e4567-e89b-12d3-a456-426614174002', patientName: 'Jane Smith' },
];

const mockRecord = {
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
};

describe('MedicalRecordForm', () => {
  const defaultProps = {
    onSubmit: jest.fn(),
    onCancel: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render form for creating new record', () => {
      render(<MedicalRecordForm {...defaultProps} />);
      
      expect(screen.getByText('Add Medical Record')).toBeInTheDocument();
      expect(screen.getByText('Create a new medical record')).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /create record/i })).toBeInTheDocument();
    });

    it('should render form for editing existing record', () => {
      render(<MedicalRecordForm {...defaultProps} record={mockRecord} />);
      
      expect(screen.getByText('Edit Medical Record')).toBeInTheDocument();
      expect(screen.getByText('Update the medical record details')).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /update record/i })).toBeInTheDocument();
    });

    it('should render all form fields', () => {
      render(<MedicalRecordForm {...defaultProps} />);
      
      expect(screen.getByLabelText(/diagnosis/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/medications/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/notes/i)).toBeInTheDocument();
    });

    it('should render patient selection for doctor role', () => {
      render(<MedicalRecordForm {...defaultProps} />);
      
      expect(screen.getByLabelText(/patient/i)).toBeInTheDocument();
      expect(screen.getByRole('combobox')).toBeInTheDocument();
    });

    it('should render modal overlay', () => {
      render(<MedicalRecordForm {...defaultProps} />);
      
      const modal = screen.getByRole('dialog');
      expect(modal).toBeInTheDocument();
      expect(modal).toHaveClass('fixed', 'inset-0');
    });
  });

  describe('Form Initialization', () => {
    it('should initialize with empty form for new record', () => {
      render(<MedicalRecordForm {...defaultProps} />);
      
      expect(screen.getByLabelText(/diagnosis/i)).toHaveValue('');
      expect(screen.getByLabelText(/medications/i)).toHaveValue('');
      expect(screen.getByLabelText(/notes/i)).toHaveValue('');
    });

    it('should initialize with existing data for edit mode', () => {
      render(<MedicalRecordForm {...defaultProps} record={mockRecord} />);
      
      expect(screen.getByLabelText(/diagnosis/i)).toHaveValue('Hypertension');
      expect(screen.getByLabelText(/medications/i)).toHaveValue('Lisinopril 10mg daily');
      expect(screen.getByLabelText(/notes/i)).toHaveValue('Patient shows improvement');
    });

    it('should disable patient selection when editing', () => {
      render(<MedicalRecordForm {...defaultProps} record={mockRecord} />);
      
      const patientSelect = screen.getByRole('combobox');
      expect(patientSelect).toBeDisabled();
    });
  });

  describe('Form Validation', () => {
    it('should show error for empty diagnosis', async () => {
      const user = userEvent.setup();
      render(<MedicalRecordForm {...defaultProps} />);
      
      const submitButton = screen.getByRole('button', { name: /create record/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(screen.getByText('Diagnosis is required')).toBeInTheDocument();
      });
    });

    it('should show error for empty patient selection', async () => {
      const user = userEvent.setup();
      render(<MedicalRecordForm {...defaultProps} />);
      
      // Fill diagnosis but leave patient empty
      await user.type(screen.getByLabelText(/diagnosis/i), 'Test Diagnosis');
      
      const submitButton = screen.getByRole('button', { name: /create record/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(screen.getByText('Please select a patient')).toBeInTheDocument();
      });
    });

    it('should clear error when user starts typing', async () => {
      const user = userEvent.setup();
      render(<MedicalRecordForm {...defaultProps} />);
      
      const submitButton = screen.getByRole('button', { name: /create record/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(screen.getByText('Diagnosis is required')).toBeInTheDocument();
      });
      
      await user.type(screen.getByLabelText(/diagnosis/i), 'Test');
      
      await waitFor(() => {
        expect(screen.queryByText('Diagnosis is required')).not.toBeInTheDocument();
      });
    });

    it('should validate diagnosis is not just whitespace', async () => {
      const user = userEvent.setup();
      render(<MedicalRecordForm {...defaultProps} />);
      
      await user.type(screen.getByLabelText(/diagnosis/i), '   ');
      
      const submitButton = screen.getByRole('button', { name: /create record/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(screen.getByText('Diagnosis is required')).toBeInTheDocument();
      });
    });
  });

  describe('Form Submission', () => {
    it('should call onSubmit with form data for new record', async () => {
      const user = userEvent.setup();
      const mockOnSubmit = jest.fn();
      render(<MedicalRecordForm {...defaultProps} onSubmit={mockOnSubmit} />);
      
      // Fill form
      await user.type(screen.getByLabelText(/diagnosis/i), 'Test Diagnosis');
      await user.type(screen.getByLabelText(/medications/i), 'Test Medication');
      await user.type(screen.getByLabelText(/notes/i), 'Test Notes');
      
      const submitButton = screen.getByRole('button', { name: /create record/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(mockOnSubmit).toHaveBeenCalledWith({
          diagnosis: 'Test Diagnosis',
          medications: 'Test Medication',
          notes: 'Test Notes',
          patientId: '',
          doctorId: 'test-user-id',
        });
      });
    });

    it('should call onSubmit with updated data for edit mode', async () => {
      const user = userEvent.setup();
      const mockOnSubmit = jest.fn();
      render(<MedicalRecordForm {...defaultProps} record={mockRecord} onSubmit={mockOnSubmit} />);
      
      // Update form
      const diagnosisField = screen.getByLabelText(/diagnosis/i);
      await user.clear(diagnosisField);
      await user.type(diagnosisField, 'Updated Diagnosis');
      
      const submitButton = screen.getByRole('button', { name: /update record/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(mockOnSubmit).toHaveBeenCalledWith({
          diagnosis: 'Updated Diagnosis',
          medications: 'Lisinopril 10mg daily',
          notes: 'Patient shows improvement',
          patientId: '123e4567-e89b-12d3-a456-426614174001',
          doctorId: '123e4567-e89b-12d3-a456-426614174002',
        });
      });
    });

    it('should show loading state during submission', async () => {
      const user = userEvent.setup();
      const mockOnSubmit = jest.fn(() => new Promise(resolve => setTimeout(resolve, 100)));
      render(<MedicalRecordForm {...defaultProps} onSubmit={mockOnSubmit} />);
      
      await user.type(screen.getByLabelText(/diagnosis/i), 'Test Diagnosis');
      
      const submitButton = screen.getByRole('button', { name: /create record/i });
      await user.click(submitButton);
      
      expect(submitButton).toBeDisabled();
      expect(screen.getByRole('status')).toBeInTheDocument();
    });

    it('should handle submission errors gracefully', async () => {
      const user = userEvent.setup();
      const mockOnSubmit = jest.fn(() => Promise.reject(new Error('Submission failed')));
      render(<MedicalRecordForm {...defaultProps} onSubmit={mockOnSubmit} />);
      
      await user.type(screen.getByLabelText(/diagnosis/i), 'Test Diagnosis');
      
      const submitButton = screen.getByRole('button', { name: /create record/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(submitButton).not.toBeDisabled();
      });
    });
  });

  describe('Form Interaction', () => {
    it('should handle input changes correctly', async () => {
      const user = userEvent.setup();
      render(<MedicalRecordForm {...defaultProps} />);
      
      const diagnosisField = screen.getByLabelText(/diagnosis/i);
      await user.type(diagnosisField, 'New Diagnosis');
      
      expect(diagnosisField).toHaveValue('New Diagnosis');
    });

    it('should handle textarea changes correctly', async () => {
      const user = userEvent.setup();
      render(<MedicalRecordForm {...defaultProps} />);
      
      const notesField = screen.getByLabelText(/notes/i);
      await user.type(notesField, 'New Notes');
      
      expect(notesField).toHaveValue('New Notes');
    });

    it('should handle patient selection', async () => {
      const user = userEvent.setup();
      render(<MedicalRecordForm {...defaultProps} />);
      
      const patientSelect = screen.getByRole('combobox');
      await user.selectOptions(patientSelect, '123e4567-e89b-12d3-a456-426614174001');
      
      expect(patientSelect).toHaveValue('123e4567-e89b-12d3-a456-426614174001');
    });

    it('should clear form when switching between create and edit modes', () => {
      const { rerender } = render(<MedicalRecordForm {...defaultProps} />);
      
      // Create mode - form should be empty
      expect(screen.getByLabelText(/diagnosis/i)).toHaveValue('');
      
      // Switch to edit mode
      rerender(<MedicalRecordForm {...defaultProps} record={mockRecord} />);
      
      // Edit mode - form should have record data
      expect(screen.getByLabelText(/diagnosis/i)).toHaveValue('Hypertension');
      
      // Switch back to create mode
      rerender(<MedicalRecordForm {...defaultProps} />);
      
      // Form should be empty again
      expect(screen.getByLabelText(/diagnosis/i)).toHaveValue('');
    });
  });

  describe('Modal Behavior', () => {
    it('should call onCancel when cancel button is clicked', async () => {
      const user = userEvent.setup();
      const mockOnCancel = jest.fn();
      render(<MedicalRecordForm {...defaultProps} onCancel={mockOnCancel} />);
      
      const cancelButton = screen.getByRole('button', { name: /cancel/i });
      await user.click(cancelButton);
      
      expect(mockOnCancel).toHaveBeenCalled();
    });

    it('should call onCancel when X button is clicked', async () => {
      const user = userEvent.setup();
      const mockOnCancel = jest.fn();
      render(<MedicalRecordForm {...defaultProps} onCancel={mockOnCancel} />);
      
      const closeButton = screen.getByTestId('x-icon').closest('button');
      await user.click(closeButton);
      
      expect(mockOnCancel).toHaveBeenCalled();
    });

    it('should call onCancel when clicking backdrop', async () => {
      const user = userEvent.setup();
      const mockOnCancel = jest.fn();
      render(<MedicalRecordForm {...defaultProps} onCancel={mockOnCancel} />);
      
      const backdrop = screen.getByRole('dialog');
      await user.click(backdrop);
      
      expect(mockOnCancel).toHaveBeenCalled();
    });

    it('should not call onCancel when clicking modal content', async () => {
      const user = userEvent.setup();
      const mockOnCancel = jest.fn();
      render(<MedicalRecordForm {...defaultProps} onCancel={mockOnCancel} />);
      
      const modalContent = screen.getByText('Add Medical Record');
      await user.click(modalContent);
      
      expect(mockOnCancel).not.toHaveBeenCalled();
    });
  });

  describe('Accessibility', () => {
    it('should have proper form labels', () => {
      render(<MedicalRecordForm {...defaultProps} />);
      
      expect(screen.getByLabelText(/diagnosis/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/medications/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/notes/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/patient/i)).toBeInTheDocument();
    });

    it('should have required field indicators', () => {
      render(<MedicalRecordForm {...defaultProps} />);
      
      expect(screen.getByText('Diagnosis *')).toBeInTheDocument();
    });

    it('should have proper button labels', () => {
      render(<MedicalRecordForm {...defaultProps} />);
      
      expect(screen.getByRole('button', { name: /create record/i })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /cancel/i })).toBeInTheDocument();
    });

    it('should have proper modal role', () => {
      render(<MedicalRecordForm {...defaultProps} />);
      
      const modal = screen.getByRole('dialog');
      expect(modal).toBeInTheDocument();
    });
  });

  describe('Edge Cases', () => {
    it('should handle very long text input', async () => {
      const user = userEvent.setup();
      render(<MedicalRecordForm {...defaultProps} />);
      
      const longText = 'A'.repeat(1000);
      await user.type(screen.getByLabelText(/diagnosis/i), longText);
      
      expect(screen.getByLabelText(/diagnosis/i)).toHaveValue(longText);
    });

    it('should handle special characters in input', async () => {
      const user = userEvent.setup();
      render(<MedicalRecordForm {...defaultProps} />);
      
      const specialText = 'Diagnosis with @#$%^&*()_+-=[]{}|;\':",./<>?';
      await user.type(screen.getByLabelText(/diagnosis/i), specialText);
      
      expect(screen.getByLabelText(/diagnosis/i)).toHaveValue(specialText);
    });

    it('should handle unicode characters', async () => {
      const user = userEvent.setup();
      render(<MedicalRecordForm {...defaultProps} />);
      
      const unicodeText = 'Diagnosis with 中文 العربية हिन्दी русский';
      await user.type(screen.getByLabelText(/diagnosis/i), unicodeText);
      
      expect(screen.getByLabelText(/diagnosis/i)).toHaveValue(unicodeText);
    });

    it('should handle form submission with minimal data', async () => {
      const user = userEvent.setup();
      const mockOnSubmit = jest.fn();
      render(<MedicalRecordForm {...defaultProps} onSubmit={mockOnSubmit} />);
      
      // Only fill required field
      await user.type(screen.getByLabelText(/diagnosis/i), 'Minimal Diagnosis');
      
      const submitButton = screen.getByRole('button', { name: /create record/i });
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(mockOnSubmit).toHaveBeenCalledWith({
          diagnosis: 'Minimal Diagnosis',
          medications: '',
          notes: '',
          patientId: '',
          doctorId: 'test-user-id',
        });
      });
    });
  });

  describe('Loading States', () => {
    it('should disable form during loading', async () => {
      const user = userEvent.setup();
      const mockOnSubmit = jest.fn(() => new Promise(resolve => setTimeout(resolve, 100)));
      render(<MedicalRecordForm {...defaultProps} onSubmit={mockOnSubmit} />);
      
      await user.type(screen.getByLabelText(/diagnosis/i), 'Test Diagnosis');
      
      const submitButton = screen.getByRole('button', { name: /create record/i });
      const cancelButton = screen.getByRole('button', { name: /cancel/i });
      
      await user.click(submitButton);
      
      expect(submitButton).toBeDisabled();
      expect(cancelButton).toBeDisabled();
    });

    it('should re-enable form after loading completes', async () => {
      const user = userEvent.setup();
      const mockOnSubmit = jest.fn(() => Promise.resolve());
      render(<MedicalRecordForm {...defaultProps} onSubmit={mockOnSubmit} />);
      
      await user.type(screen.getByLabelText(/diagnosis/i), 'Test Diagnosis');
      
      const submitButton = screen.getByRole('button', { name: /create record/i });
      const cancelButton = screen.getByRole('button', { name: /cancel/i });
      
      await user.click(submitButton);
      
      await waitFor(() => {
        expect(submitButton).not.toBeDisabled();
        expect(cancelButton).not.toBeDisabled();
      });
    });
  });
});
