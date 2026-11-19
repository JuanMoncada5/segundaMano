/** Espejo de LoginBody (POST /api/auth/login). */
export interface LoginRequest {
  correoInstitucional: string;
  contrasena: string;
}

/** Espejo de LoginResponse. rol: 'UNICAMBISTA' | 'ADMIN'. */
export interface LoginResponse {
  token: string;
  tipo: string;
  expiraEn: string;
  correoInstitucional: string;
  rol: string;
}
