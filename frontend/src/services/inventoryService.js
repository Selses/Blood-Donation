import api from './api';

export const inventoryService = {
  getMyInventory: async () => {
    const response = await api.get('/api/inventory');
    return response.data;
  },

  searchByBloodGroup: async (bloodGroup) => {
    const response = await api.get(`/api/inventory/blood-group/${encodeURIComponent(bloodGroup)}`);
    return response.data;
  },

  createOrUpdateInventory: async (inventoryData) => {
    const response = await api.post('/api/inventory', inventoryData);
    return response.data;
  },

  updateInventory: async (id, inventoryData) => {
    const response = await api.put(`/api/inventory/${id}`, inventoryData);
    return response.data;
  },

  addUnits: async (id, units) => {
    const response = await api.patch(`/api/inventory/${id}/add`, { units });
    return response.data;
  },

  removeUnits: async (id, units) => {
    const response = await api.patch(`/api/inventory/${id}/remove`, { units });
    return response.data;
  }
};
