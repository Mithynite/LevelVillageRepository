import { Routes, Route } from 'react-router-dom';
import Login from './pages/Login.jsx';
import Home from './pages/Home.jsx';
import SignUpPage from "./pages/SignUp";
import Welcome from "./pages/Welcome.jsx";
import ProtectedRoute from "./api/ProtectedRoute.jsx";
import PostDetails from "./pages/post/PostDetails.jsx";
import UserProfile from "./pages/user/UserProfile.jsx";
import PostCreation from "./pages/post/PostCreation.jsx";
import UserLikedPosts from "./pages/user/UserLikedPosts.jsx";
import Notifications from "./pages/functions/Notifications.jsx";

function App() {
    return (
        <Routes>
            <Route path="/" element={<Welcome />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<SignUpPage />} />
            <Route element={<ProtectedRoute/>}>
                <Route path="/home" element={<Home />}/>
                <Route path="/posts/:id" element={<PostDetails isMyPost={false} />} />
                <Route path="/posts/create" element={<PostCreation/>} />
                <Route path="/myposts/:id" element={<PostDetails isMyPost={true} />} />
                <Route path="/users/:username/profile" element={<UserProfile />} />
                <Route path="/users/:username/liked-posts" element={<UserLikedPosts />} />
                <Route path="/users/:username/notifications" element={<Notifications />} />
            </Route>
        </Routes>
    );
}

export default App;
