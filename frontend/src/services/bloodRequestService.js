import api from './api';

export const bloodRequestService = {
  getAllRequests: async () => {
    const response = await api.get('/api/blood-requests');
    return response.data;
  },

  getRequestById: async (id) => {
    const response = await api.get(`/api/blood-requests/${id}`);
    return response.data;
  },

  createRequest: async (requestData) => {
    const response = await api.post('/api/blood-requests', requestData);
    return response.data;
  },

  getMatchesForRequest: async (id) => {
    const response = await api.get(`/api/blood-requests/${id}/matches`);
    return response.data;
  },

  acceptMatch: async (requestId, matchId) => {
    const response = await api.post(`/api/blood-requests/${requestId}/matches/${matchId}/accept`);
    return response.data;
  },

  declineMatch: async (requestId, matchId) => {
    const response = await api.post(`/api/blood-requests/${requestId}/matches/${matchId}/decline`);
    return response.data;
  },

  fulfillRequest: async (id, fulfillData = {}) => {
    const response = await api.post(`/api/blood-requests/${id}/fulfill`, fulfillData);
    return response.data;
  },

  cancelRequest: async (id) => {
    const response = await api.patch(`/api/blood-requests/${id}/cancel`);
    return response.data;
  },

  searchRequests: async (params) => {
    const response = await api.get('/api/blood-requests/search', { params });
    return response.data;
  }
};
