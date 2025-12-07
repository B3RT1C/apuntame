export const UI_MESSAGES = {
  // Acciones comunes
  CLOSE: 'Cerrar',
  CONFIRM: 'Confirmar',
  CANCEL: 'Cancelar',
  DELETE: 'Eliminar',
  SAVE: 'Guardar',

  // Mensajes de éxito CRUD
  USER_CREATED: 'Usuario creado exitosamente',
  USER_UPDATED: 'Usuario actualizado exitosamente',
  USER_DELETED: 'Usuario eliminado exitosamente',
  ITEM_CREATED: 'Producto creado exitosamente',
  ITEM_UPDATED: 'Producto actualizado exitosamente',
  ITEM_DELETED: 'Producto eliminado exitosamente',
  CATEGORY_CREATED: 'Categoría creada exitosamente',
  CATEGORY_UPDATED: 'Categoría actualizada exitosamente',
  CATEGORY_DELETED: 'Categoría eliminada exitosamente',
  SECTION_CREATED: 'Sección creada exitosamente',
  SECTION_UPDATED: 'Sección actualizada exitosamente',
  SECTION_DELETED: 'Sección eliminada exitosamente',

  // Mensajes de error
  BAD_CREDENTIALS: 'Usuario o contraseña incorrectos',
  RESOURCE_NOT_FOUND: 'Recurso no encontrado',
  RESOURCE_EXISTS: 'El recurso ya existe',
  INVALID_DATA: 'Datos inválidos proporcionados',
  SERVER_ERROR: 'Error del servidor, intente nuevamente',
  ELEMENT_ALREADY_DELETED: 'El elemento ya había sido eliminado. Tabla actualizada.',

  // Títulos de diálogos
  DELETE_USER_TITLE: 'Eliminar Usuario',
  DELETE_ITEM_TITLE: 'Eliminar Producto',
  DELETE_CATEGORY_TITLE: 'Eliminar Categoría',
  DELETE_SECTION_TITLE: 'Eliminar Sección',
  EDIT_USER_TITLE: 'Editar Usuario',
  CREATE_USER_TITLE: 'Crear Usuario',
  EDIT_ITEM_TITLE: 'Editar Artículo',
  CREATE_ITEM_TITLE: 'Crear Artículo',
  EDIT_CATEGORY_TITLE: 'Editar Categoría',
  CREATE_CATEGORY_TITLE: 'Crear Categoría',
  EDIT_SECTION_TITLE: 'Editar Sección',
  CREATE_SECTION_TITLE: 'Crear Sección',

  // Mensajes de confirmación (funciones para interpolación)
  confirmDeleteUser: (username: string) => `¿Está seguro que desea eliminar el usuario "${username}"?`,
  confirmDeleteItem: (name: string) => `¿Está seguro que desea eliminar el producto "${name}"?`,
  confirmDeleteCategory: (name: string) => `¿Está seguro que desea eliminar la categoría "${name}"?`,
  confirmDeleteSection: (name: string) => `¿Está seguro que desea eliminar la sección "${name}"?`
};
