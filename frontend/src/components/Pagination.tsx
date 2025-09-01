import React from "react";

interface Props {
    total: number;
    limit: number;
    offset: number;
    onPageChange: (pageIndex: number) => void;
}

export default function Pagination({ total, limit, offset, onPageChange }: Props) {
    const per = Math.max(1, limit);
    const pages = Math.max(1, Math.ceil(total / per));
    const current = Math.floor(offset / per);

    if (pages <= 1) return null;

    const goto = (i: number) => {
        if (i < 0 || i >= pages) return;
        onPageChange(i);
    };

    const Btn = ({ label, i, active = false }: { label: string; i: number; active?: boolean }) => (
        <button
            key={label + i}
            className={`px-2 py-1 rounded ${active ? "bg-gray-900 text-white" : "bg-gray-200"}`}
            onClick={() => goto(i)}
        >
            {label}
        </button>
    );

    const items: React.ReactNode[] = [];
    items.push(<Btn label="«" i={current - 1} key="prev" />);
    for (let i = Math.max(0, current - 2); i < Math.min(pages, current + 3); i++) {
        items.push(<Btn label={String(i + 1)} i={i} key={i} active={i === current} />);
    }
    items.push(<Btn label="»" i={current + 1} key="next" />);

    return <div className="flex items-center gap-2">{items}</div>;
}
