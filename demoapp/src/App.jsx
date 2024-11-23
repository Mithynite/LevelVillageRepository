import { Routes, Route } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import HomePage from './pages/HomePage.jsx';
import SignUpPage from "./pages/SignUp";
import WelcomePage from "./pages/WelcomePage.jsx";
import ProtectedRoute from "./api/ProtectedRoute.jsx";

function App() {
    return (
        <Routes>
            <Route path="/" element={<WelcomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/signup" element={<SignUpPage />} />
            <Route path="/home" element={<ProtectedRoute><HomePage /></ProtectedRoute>} />
        </Routes>
    );
}

export default App;
