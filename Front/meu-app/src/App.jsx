import { Routes, Route, Navigate } from "react-router-dom";
import CadastroMorador from "./pages/cadastroMorador";
// function PrivateRoute({ path, element: Component, allowedTypes }) {
//   const isAuthenticated = sessionStorage.getItem('dados') !== null;
//   const tipoUsuario = isAuthenticated ? JSON.parse(sessionStorage.getItem('dados')).tipoUsuario : '';

//   if (!isAuthenticated) {
//     return <Navigate to="/login" />;
//   }

//   if (!allowedTypes.includes(tipoUsuario)) {
//     return <Navigate to="/acesso-negado" />;
//   }

//   return <Component />;
// }

function App() {
  return (
    <Routes>
      <Route path="/cadastro-morador" element={<CadastroMorador />} />
    </Routes>
  );
}

export default App;
