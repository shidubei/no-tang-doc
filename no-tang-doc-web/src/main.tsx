import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import './index.css';
import { AppRoutes } from './routes';
import { ThemeProvider } from './components/ThemeContext';
import { AuthProvider } from './components/AuthContext';
import { Toaster } from './components/ui/sonner';
import {HomePage} from "./pages/HomePage.tsx";

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <BrowserRouter>
            <ThemeProvider>
                <AuthProvider>
                    <AppRoutes />
                    <Toaster />
                </AuthProvider>
            </ThemeProvider>
        </BrowserRouter>
    </React.StrictMode>
);

// 若暂时仍未真正使用路由切换, 可以撤回上面集中路由的改动, 只先修复 HomePage 导入导出即可。

// 临时调试: 在任一使用处加:
console.log('Debug HomePage is function:', typeof HomePage === 'function');