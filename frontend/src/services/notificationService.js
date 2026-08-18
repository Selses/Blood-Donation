import api from './api';

export const notificationService = {
  getMyNotifications: async () => {
    const response = await api.get('/api/notifications');
    return response.data;
  },

  getUnreadNotifications: async () => {
    const response = await api.get('/api/notifications/unread');
    return response.data;
  },

  getUnreadCount: async () => {
    const response = await api.get('/api/notifications/unread/count');
    return response.data;
  },

  markAsRead: async (id) => {
    const response = await api.patch(`/api/notifications/${id}/read`);
    return response.data;
  },

  markAllAsRead: async () => {
    const response = await api.patch('/api/notifications/read-all');
    return response.data;
  }
};
