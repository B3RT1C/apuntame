export class ApiEndpoints {
  static readonly AUTH = {
    BASE: '/auth',
    LOGIN: '/auth/login'
  };

  static readonly USERS = {
    BASE: '/users',
    BY_USERNAME: (username: string) => `/users/${username}`,
    ORDERS: (username: string) => `/users/${username}/orders`
  };

  static readonly ITEMS = {
    BASE: '/items',
    BY_ID: (id: number) => `/items/${id}`
  };

  static readonly ORDERS = {
    BASE: '/orders',
    BY_ID: (id: number) => `/orders/${id}`,
    ITEMS: (orderId: number) => `/orders/${orderId}/items`,
    ITEMS_BATCH: (orderId: number) => `/orders/${orderId}/items/batch`,
    ITEM_BY_ID: (orderId: number, itemId: number) => `/orders/${orderId}/items/${itemId}`
  };
}
