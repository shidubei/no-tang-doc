import { Routes, Route } from 'react-router-dom';
import { Suspense, lazy } from 'react';

// 懒加载命名导出组件需在模块里有默认导出才方便, 所以这里示例保持 App 默认导出
const App = lazy(() => import('@/App'));

// 如果 HomePage 是命名导出, 不能直接 lazy 默认方式; 可改成一个包裹:
// 也可以给 HomePage 增加一个默认导出避免复杂度.
import { HomePage } from '@/pages/HomePage';

export function AppRoutes() {
    return (
        <Suspense fallback={<div>Loading...</div>}>
            <Routes>
                {/* 将原 /app 对应的老 App 先保持, 后续再拆 */}
                <Route path="/app" element={<App />} />
                <Route path="/" element={
                    <HomePage
                        onNavigateToAuth={() => {/* 占位: 旧状态路由逻辑未迁移前可留空 */}}
                        onNavigateToDashboard={() => {}}
                        onNavigateHome={() => {}}
                        onStartUploading={() => {}}
                    />
                } />
            </Routes>
        </Suspense>
    );
}