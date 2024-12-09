import { Routes, Route } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import HomePage from './pages/HomePage.jsx';
import SignUpPage from "./pages/SignUp";
import WelcomePage from "./pages/WelcomePage.jsx";
import ProtectedRoute from "./api/ProtectedRoute.jsx";
import PostDetails from "./pages/post/PostDetails.jsx";
import ProfilePage from "./pages/ProfilePage.jsx";

function App() {
    return (
        <Routes>
            <Route path="/" element={<WelcomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/signup" element={<SignUpPage />} />
            <Route element={<ProtectedRoute/>}>
                <Route path="/home" element={<HomePage />}/>
                <Route path="/posts/:id" element={<PostDetails isMyPost={false} />} />
                <Route path="/myposts/:id" element={<PostDetails isMyPost={true} />} />
                <Route path="/profile" element={<ProfilePage />} />
            </Route>
        </Routes>
    );
}

export default App;
