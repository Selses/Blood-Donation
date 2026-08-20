import React, { useState, useEffect } from 'react';
import { inventoryService } from '../services/inventoryService';
import { useAuth } from '../context/AuthContext';
import { Package, Plus, Minus, PlusCircle, Search, AlertCircle, CheckCircle2 } from 'lucide-react';

const Inventory = () => {
  const { user } = useAuth();
  const [inventoryList, setInventoryList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Search by blood group
  const [selectedGroup, setSelectedGroup] = useState('');

  // Add / Create Modal state
  const [showAddModal, setShowAddModal] = useState(false);
  const [newGroup, setNewGroup] = useState('O+');
  const [newUnits, setNewUnits] = useState(10);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    fetchInventory();
  }, [selectedGroup]);

  const fetchInventory = async () => {
    setLoading(true);
    setError('');
    try {
      let data;
      if (selectedGroup) {
        data = await inventoryService.searchByBloodGroup(selectedGroup);
      } else {
        data = await inventoryService.getMyInventory();
      }
      setInventoryList(data);
    } catch (err) {
      setError(err.message || 'Unable to load blood inventory.');
    } finally {
      setLoading(false);
    }
  };

  const handleAddUnits = async (id, currentUnits) => {
    const unitsToAdd = prompt('Enter number of blood units to ADD:', '5');
    if (!unitsToAdd || isNaN(unitsToAdd) || parseInt(unitsToAdd, 10) <= 0) return;

    setActionLoading(true);
    setError('');
    setSuccessMsg('');
    try {
      await inventoryService.addUnits(id, parseInt(unitsToAdd, 10));
      setSuccessMsg(`Successfully added ${unitsToAdd} units.`);
      fetchInventory();
    } catch (err) {
      setError(err.message || 'Failed to add units.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleRemoveUnits = async (id, currentUnits) => {
    const unitsToRemove = prompt(`Enter number of blood units to REMOVE (Available: ${currentUnits}):`, '1');
    if (!unitsToRemove || isNaN(unitsToRemove) || parseInt(unitsToRemove, 10) <= 0) return;

    setActionLoading(true);
    setError('');
    setSuccessMsg('');
    try {
      await inventoryService.removeUnits(id, parseInt(unitsToRemove, 10));
      setSuccessMsg(`Successfully removed ${unitsToRemove} units.`);
      fetchInventory();
    } catch (err) {
      setError(err.message || 'Failed to remove units.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleCreateInventory = async (e) => {
    e.preventDefault();
    setActionLoading(true);
    setError('');
    setSuccessMsg('');
    try {
      await inventoryService.createOrUpdateInventory({
        bloodGroup: newGroup,
        availableUnits: newUnits
      });
      setSuccessMsg(`Inventory record for ${newGroup} created/updated!`);
      setShowAddModal(false);
      fetchInventory();
    } catch (err) {
      setError(err.message || 'Failed to create inventory.');
    } finally {
      setActionLoading(false);
    }
  };

  const bloodGroups = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

  return (
    <div className="container inventory-page">
      <div className="page-header">
        <div>
          <h1>Hospital & Blood Bank Inventory</h1>
          <p className="text-muted">Manage available blood units, track stock levels, and fulfill emergencies.</p>
        </div>

        <button onClick={() => setShowAddModal(true)} className="btn btn-primary">
          <PlusCircle size={18} />
          <span>Add Blood Stock</span>
        </button>
      </div>

      {error && (
        <div className="alert alert-danger">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {successMsg && (
        <div className="alert alert-success">
          <CheckCircle2 size={18} />
          <span>{successMsg}</span>
        </div>
      )}

      {/* Blood Group Filter Bar */}
      <div className="card filter-card">
        <div className="filter-title">Filter by Blood Group:</div>
        <div className="blood-group-pills">
          <button 
            className={`group-pill ${selectedGroup === '' ? 'active' : ''}`}
            onClick={() => setSelectedGroup('')}
          >
            All Stock
          </button>
          {bloodGroups.map(bg => (
            <button 
              key={bg} 
              className={`group-pill ${selectedGroup === bg ? 'active' : ''}`}
              onClick={() => setSelectedGroup(bg)}
            >
              {bg}
            </button>
          ))}
        </div>
      </div>

      {/* Inventory Grid */}
      <div className="inventory-grid">
        {loading ? (
          <div className="card loading-state col-span-full">
            <Package className="animate-spin text-primary mx-auto mb-2" size={32} />
            <p>Loading inventory stock...</p>
          </div>
        ) : inventoryList.length === 0 ? (
          <div className="card empty-state col-span-full">
            <Package size={36} className="text-dim mx-auto mb-2" />
            <p>No inventory records found for the selected blood group.</p>
          </div>
        ) : (
          inventoryList.map(item => (
            <div key={item.id} className="card inventory-item-card">
              <div className="item-header">
                <span className="blood-group-pill">{item.bloodGroup}</span>
                <span className="org-tag">{item.hospitalName || item.bloodBankName || 'Organization'}</span>
              </div>

              <div className="units-display">
                <div className="units-number">{item.availableUnits}</div>
                <div className="units-label">Units Available</div>
              </div>

              <div className="item-actions">
                <button 
                  onClick={() => handleAddUnits(item.id, item.availableUnits)}
                  className="btn btn-secondary btn-sm flex-1"
                  disabled={actionLoading}
                >
                  <Plus size={14} /> Add
                </button>
                <button 
                  onClick={() => handleRemoveUnits(item.id, item.availableUnits)}
                  className="btn btn-secondary btn-sm flex-1"
                  disabled={actionLoading || item.availableUnits <= 0}
                >
                  <Minus size={14} /> Remove
                </button>
              </div>

              <div className="item-footer">
                Last updated: {item.lastUpdated ? new Date(item.lastUpdated).toLocaleDateString() : 'Recent'}
              </div>
            </div>
          ))
        )}
      </div>

      {/* Add Stock Modal */}
      {showAddModal && (
        <div className="modal-backdrop">
          <div className="modal-card card">
            <h3>Add / Update Blood Stock</h3>
            <form onSubmit={handleCreateInventory}>
              <div className="form-group">
                <label className="form-label">Blood Group</label>
                <select 
                  className="form-select"
                  value={newGroup}
                  onChange={(e) => setNewGroup(e.target.value)}
                >
                  {bloodGroups.map(bg => (
                    <option key={bg} value={bg}>{bg}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Available Units</label>
                <input 
                  type="number"
                  className="form-input"
                  min="0"
                  max="1000"
                  value={newUnits}
                  onChange={(e) => setNewUnits(parseInt(e.target.value, 10) || 0)}
                  required
                />
              </div>

              <div className="modal-actions">
                <button 
                  type="button" 
                  onClick={() => setShowAddModal(false)}
                  className="btn btn-secondary"
                >
                  Cancel
                </button>
                <button 
                  type="submit" 
                  className="btn btn-primary"
                  disabled={actionLoading}
                >
                  Save Stock
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <style>{`
        .inventory-page {
          padding-top: 1.5rem;
          padding-bottom: 3rem;
        }
        .page-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 1.5rem;
        }
        .page-header h1 {
          font-size: 1.5rem;
          font-weight: 800;
        }
        .filter-card {
          display: flex;
          align-items: center;
          gap: 1rem;
          padding: 1rem 1.25rem;
          margin-bottom: 1.5rem;
          flex-wrap: wrap;
        }
        .filter-title {
          font-size: 0.8125rem;
          font-weight: 700;
          color: var(--text-dim);
          text-transform: uppercase;
        }
        .blood-group-pills {
          display: flex;
          gap: 0.5rem;
          flex-wrap: wrap;
        }
        .group-pill {
          padding: 0.35rem 0.75rem;
          border-radius: var(--radius-full);
          background: #334155;
          border: 1px solid transparent;
          color: var(--text-muted);
          font-weight: 700;
          font-size: 0.8125rem;
          cursor: pointer;
          transition: all 0.2s ease;
        }
        .group-pill:hover {
          color: white;
          background: #475569;
        }
        .group-pill.active {
          background: var(--primary);
          color: white;
        }
        .inventory-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
          gap: 1rem;
        }
        .col-span-full { grid-column: 1 / -1; }
        .inventory-item-card {
          padding: 1.25rem;
          display: flex;
          flex-direction: column;
        }
        .item-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 1rem;
        }
        .blood-group-pill {
          font-size: 1.25rem;
          font-weight: 900;
          color: var(--primary);
          background: rgba(225, 29, 72, 0.15);
          padding: 0.25rem 0.625rem;
          border-radius: var(--radius-md);
        }
        .org-tag {
          font-size: 0.6875rem;
          color: var(--text-dim);
          font-weight: 600;
          max-width: 120px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        .units-display {
          text-align: center;
          margin: 0.75rem 0 1.25rem;
        }
        .units-number {
          font-size: 2.5rem;
          font-weight: 900;
          color: white;
          line-height: 1;
        }
        .units-label {
          font-size: 0.75rem;
          color: var(--text-muted);
          margin-top: 0.25rem;
        }
        .item-actions {
          display: flex;
          gap: 0.5rem;
          margin-bottom: 0.75rem;
        }
        .flex-1 { flex: 1; }
        .item-footer {
          font-size: 0.6875rem;
          color: var(--text-dim);
          text-align: center;
          border-top: 1px solid var(--border);
          padding-top: 0.5rem;
          margin-top: auto;
        }
        .modal-backdrop {
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.7);
          backdrop-filter: blur(4px);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 100;
        }
        .modal-card {
          width: 100%;
          max-width: 400px;
          padding: 1.75rem;
        }
        .modal-card h3 {
          margin-bottom: 1.25rem;
        }
        .modal-actions {
          display: flex;
          justify-content: flex-end;
          gap: 0.75rem;
          margin-top: 1.5rem;
        }
        @media (max-width: 640px) {
          .page-header { flex-direction: column; align-items: flex-start; gap: 1rem; }
        }
      `}</style>
    </div>
  );
};

export default Inventory;
