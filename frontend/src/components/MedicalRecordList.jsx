import React from 'react';
import { motion } from 'framer-motion';
import { 
  FileText, 
  Edit, 
  Trash2, 
  Calendar, 
  User, 
  Stethoscope,
  MessageSquare,
  Clock
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from './ui/card';
import { Button } from './ui/button';
import { Badge } from './ui/badge';

const MedicalRecordList = ({ 
  records, 
  loading, 
  error, 
  onEdit, 
  onDelete, 
  userRole,
  searchQuery = '',
  onSearchChange 
}) => {
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'bg-green-100 text-green-800';
      case 'ARCHIVED': return 'bg-gray-100 text-gray-800';
      default: return 'bg-blue-100 text-blue-800';
    }
  };

  const highlightSearchTerm = (text, searchTerm) => {
    if (!searchTerm || !text) return text;
    
    const regex = new RegExp(`(${searchTerm})`, 'gi');
    return text.replace(regex, '<mark class="bg-yellow-200">$1</mark>');
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">Loading medical records...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <Card>
        <CardContent className="p-12 text-center">
          <div className="text-red-500 mb-4">
            <FileText className="h-16 w-16 mx-auto" />
          </div>
          <h3 className="text-lg font-semibold text-foreground mb-2">Error Loading Records</h3>
          <p className="text-muted-foreground">{error}</p>
        </CardContent>
      </Card>
    );
  }

  if (!records || records.length === 0) {
    return (
      <Card>
        <CardContent className="p-12 text-center">
          <FileText className="h-16 w-16 text-muted-foreground mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-foreground mb-2">
            {searchQuery ? 'No records found' : 'No medical records yet'}
          </h3>
          <p className="text-muted-foreground">
            {searchQuery 
              ? 'Try adjusting your search criteria'
              : userRole === 'DOCTOR' 
                ? 'Start by adding a new medical record'
                : 'Your medical records will appear here'
            }
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="grid gap-6">
      {records.map((record, index) => (
        <motion.div
          key={record.recordId}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: index * 0.1 }}
        >
          <Card className="hover:shadow-lg transition-shadow">
            <CardHeader>
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <CardTitle className="text-xl mb-2">
                    {searchQuery ? (
                      <span 
                        dangerouslySetInnerHTML={{
                          __html: highlightSearchTerm(record.diagnosis, searchQuery)
                        }}
                      />
                    ) : (
                      record.diagnosis
                    )}
                  </CardTitle>
                  
                  <div className="flex items-center space-x-4 text-sm text-muted-foreground mb-2">
                    <div className="flex items-center space-x-1">
                      <User className="h-4 w-4" />
                      <span>
                        {userRole === 'DOCTOR' ? record.patientName : record.doctorName}
                      </span>
                    </div>
                    <div className="flex items-center space-x-1">
                      <Calendar className="h-4 w-4" />
                      <span>{formatDate(record.createdAt)}</span>
                    </div>
                    {record.updatedAt && record.updatedAt !== record.createdAt && (
                      <div className="flex items-center space-x-1">
                        <Clock className="h-4 w-4" />
                        <span>Updated {formatDate(record.updatedAt)}</span>
                      </div>
                    )}
                  </div>

                  {/* Record ID Badge */}
                  <div className="flex items-center space-x-2">
                    <Badge variant="outline" className="text-xs">
                      ID: {record.recordId.slice(0, 8)}...
                    </Badge>
                  </div>
                </div>
                
                {userRole === 'DOCTOR' && (
                  <div className="flex items-center space-x-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => onEdit && onEdit(record)}
                      title="Edit record"
                    >
                      <Edit className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => onDelete && onDelete(record.recordId)}
                      className="text-red-600 hover:text-red-700"
                      title="Delete record"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                )}
              </div>
            </CardHeader>
            
            <CardContent className="space-y-4">
              {/* Medications Section */}
              {record.medications && (
                <div>
                  <h4 className="font-semibold text-foreground mb-2 flex items-center">
                    <Stethoscope className="h-4 w-4 mr-2" />
                    Medications
                  </h4>
                  <div className="text-muted-foreground">
                    {searchQuery ? (
                      <span 
                        dangerouslySetInnerHTML={{
                          __html: highlightSearchTerm(record.medications, searchQuery)
                        }}
                      />
                    ) : (
                      record.medications
                    )}
                  </div>
                </div>
              )}
              
              {/* Notes Section */}
              {record.notes && (
                <div>
                  <h4 className="font-semibold text-foreground mb-2 flex items-center">
                    <MessageSquare className="h-4 w-4 mr-2" />
                    Notes
                  </h4>
                  <div className="text-muted-foreground">
                    {searchQuery ? (
                      <span 
                        dangerouslySetInnerHTML={{
                          __html: highlightSearchTerm(record.notes, searchQuery)
                        }}
                      />
                    ) : (
                      record.notes
                    )}
                  </div>
                </div>
              )}

              {/* Additional Info */}
              <div className="pt-2 border-t border-gray-100">
                <div className="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <span className="font-medium text-foreground">Patient ID:</span>
                    <span className="ml-2 text-muted-foreground">
                      {record.patientId.slice(0, 8)}...
                    </span>
                  </div>
                  <div>
                    <span className="font-medium text-foreground">Doctor ID:</span>
                    <span className="ml-2 text-muted-foreground">
                      {record.doctorId.slice(0, 8)}...
                    </span>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </motion.div>
      ))}
    </div>
  );
};

export default MedicalRecordList;
